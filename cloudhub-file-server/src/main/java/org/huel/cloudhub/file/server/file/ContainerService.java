package org.huel.cloudhub.file.server.file;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.container.*;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.fs.meta.SerializeContainerBlockMeta;
import org.huel.cloudhub.file.fs.meta.SerializeContainerFileMeta;
import org.huel.cloudhub.file.fs.meta.SerializeContainerMeta;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.io.IOException;
import java.util.*;

/**
 * @author RollW
 */
@Service
public class ContainerService implements ContainerAllocator {
    private final Cache<String, ContainersHolder> containerCache =
            Caffeine.newBuilder().build();
    private final FileProperties fileProperties;
    private final LocalFileServer localFileServer;

    private final Logger logger = LoggerFactory.getLogger(ContainerService.class);

    private final ServerFile containerDir;

    public ContainerService(FileProperties fileProperties,
                            LocalFileServer localFileServer) throws IOException {
        this.fileProperties = fileProperties;
        this.localFileServer = localFileServer;
        this.containerDir =
                localFileServer.getServerFileProvider().openFile(fileProperties.getContainerPath());
        readContainer();
    }

    private void readContainer() throws IOException {
        ServerFile metaDir =
                localFileServer.getServerFileProvider().openFile(fileProperties.getMetaPath());
        metaDir.mkdirs();
        containerDir.mkdirs();

        List<ServerFile> metaFiles = metaDir.listFiles();
        List<String> locators = new ArrayList<>();
        for (ServerFile metaFile : metaFiles) {
            SerializeContainerFileMeta fileMeta = readContainerMeta(metaFile);
            fileMeta.getMetaList().forEach(meta ->
                    locators.add(meta.getLocator()));
        }

        for (String locator : locators) {
            ServerFile file = localFileServer.getServerFileProvider()
                    .openFile(containerDir, locator);
            ServerFile metaFile = localFileServer.getServerFileProvider()
                    .openFile(containerDir, locator + ContainerLocation.META_SUFFIX);
            SerializeContainerBlockMeta containerBlockMeta = readContainerBlockMeta(metaFile);
            List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
            containerBlockMeta.getBlockMetasList().forEach(serializeBlockFileMeta ->
                    blockMetaInfos.add(BlockMetaInfo.deserialize(serializeBlockFileMeta)));

            ContainerFileNameMeta fileNameMeta = ContainerFileNameMeta.parse(locator);
            ContainerIdentity identity = new ContainerIdentity(
                    fileNameMeta.id(),
                    containerBlockMeta.getCrc(),
                    fileNameMeta.serial(),
                    fileNameMeta.version(),
                    containerBlockMeta.getBlockCap(),
                    containerBlockMeta.getBlockSize());

            ContainerLocation location =
                    new ContainerLocation(file.getPath());
            Container container = new Container(
                    location,
                    containerBlockMeta.getUsedBlock(),
                    fileNameMeta, identity,
                    blockMetaInfos,true);
            updatesContainer(container);
        }
    }

    /**
     * Allocates a container.
     *
     * @param id id
     * @return {@link Container}
     */
    @Override
    @NonNull
    public Container allocateContainer(String id) {
        id = ContainerIdentity.toMetaId(id);

        ContainersHolder holder = containerCache.get(id,
                s -> new ContainersHolder());

        if (holder == null) {
            logger.info("holder null");

            Container container = createsNewContainer(id, 1, 1);
            ContainersHolder newHolder = new ContainersHolder();

            newHolder.put(container);
            containerCache.put(id, newHolder);
            return container;
        }

        // FIXME: cannot find ava container correctly.
        Container container = holder.latestAvailableContainer();
        if (container != null) {
            logger.info("find available container: {}", container.getResourceLocator());
            return container;
        }
        container = createsNewContainer(id, holder.lastSerial() + 1, 1);
        logger.info("not find available container, creates new {}", container.getResourceLocator());
        holder.put(container);
        return container;
    }

    @Override
    public boolean dataExists(String fileId) {
        // TODO
        return false;
    }

    @Override
    public void createsContainerFileWithMeta(Container container) throws IOException {
        logger.info("creates container, locator: {}, ", container.getResourceLocator());
        ServerFile containerFile = localFileServer.getServerFileProvider().openFile(fileProperties.getContainerPath(),
                container.getResourceLocator());
        ServerFile containerMetaFile = localFileServer.getServerFileProvider().openFile(fileProperties.getContainerPath(),
                container.getResourceLocator() + ContainerLocation.META_SUFFIX);
        ServerFile metaFile = localFileServer.getServerFileProvider().openFile(
                fileProperties.getMetaPath(),
                ContainerIdentity.toCmetaId(container.getResourceLocator()) + CONTAINER_META_FILE_SUFFIX);
        containerFile.createFile();
        containerMetaFile.createFile();
        metaFile.createFile();

        var meta =
                SerializeContainerMeta.newBuilder().setLocator(container.getResourceLocator()).build();
        writeContainerMeta(meta);
        container.setValid();
    }

    @Override
    public void updatesContainerMetadata(Container container) throws MetaException, IOException {
        if (!container.isValid()) {
            throw new MetaException("Not valid container.");
        }
        ServerFile containerMetaFile = localFileServer.getServerFileProvider().openFile(fileProperties.getContainerPath(),
                container.getResourceLocator() + ContainerLocation.META_SUFFIX);
        SerializeContainerBlockMeta containerBlockMeta = SerializeContainerBlockMeta.newBuilder()
                .setBlockSize(container.getIdentity().blockSize())
                .setBlockCap(container.getIdentity().blockLimit())
                .setUsedBlock(container.getUsedBlock())
                .addAllBlockMetas(container.getSerializeMetaInfos())
                .setCrc(container.getIdentity().crc())
                .build();

        updatesContainer(container);
        containerBlockMeta.writeTo(containerMetaFile.openOutput(true));
    }

    private Container createsNewContainer(String id, long serial, long version) {
        ContainerFileNameMeta fileNameMeta = new ContainerFileNameMeta(
                id, serial, version);
        ContainerIdentity identity = new ContainerIdentity(
                fileNameMeta.id(),
                ContainerIdentity.INVALID_CRC,
                fileNameMeta.serial(),
                fileNameMeta.version(),
                fileProperties.getBlockCount(),
                fileProperties.getBlockSize());

        ContainerLocation location = new ContainerLocation(
                ContainerLocation.toDataPath(containerDir, fileNameMeta.toName())
        );

        return new Container(location,
                0,
                fileNameMeta,
                identity, null, false);
    }

    public Collection<Container> listContainers(String id) {
        ContainersHolder holder =
                containerCache.get(id, s -> new ContainersHolder());
        if (holder == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(holder.containers.values());
    }

    public Collection<Container> listContainers() {
        return containerCache.asMap().values().stream().collect(
                ArrayList::new,
                (containers, containersHolder) ->
                        containers.addAll(containersHolder.containers.values()),
                ArrayList::addAll
        );
    }

    @Async
    void updatesContainer(Container container) {
        ContainersHolder containers =
                containerCache.getIfPresent(container.getResourceLocator());
        if (containers == null) {
            containers = new ContainersHolder();
        }
        containers.put(container);
        containerCache.put(container.getResourceLocator(), containers);
    }

    private SerializeContainerBlockMeta readContainerBlockMeta(ServerFile file) throws IOException {
        return SerializeContainerBlockMeta.parseFrom(file.openInput());
    }

    @Async
    void writeContainerMeta(SerializeContainerMeta containerMeta) throws IOException {
        ContainerFileNameMeta fileNameMeta =
                ContainerFileNameMeta.parse(containerMeta.getLocator());
        String fileName = ContainerIdentity.toCmetaId(fileNameMeta.id());

        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(fileProperties.getMetaPath(), fileName + CONTAINER_META_FILE_SUFFIX);
        boolean createState = file.createFile();
        SerializeContainerFileMeta containerFileMeta =
                SerializeContainerFileMeta.parseFrom(file.openInput());
        List<SerializeContainerMeta> containerMetas = new ArrayList<>(containerFileMeta.getMetaList());
        containerMetas.add(containerMeta);
        containerMetas = containerMetas.stream().distinct().toList();
        containerFileMeta = SerializeContainerFileMeta.newBuilder()
                .addAllMeta(containerMetas)
                .build();
        containerFileMeta.writeTo(
                file.openOutput(true));
    }

    public SerializeContainerFileMeta readContainerMeta(ServerFile serverFile) throws IOException {
        return SerializeContainerFileMeta.parseFrom(serverFile.openInput());
    }

    private static class ContainersHolder {
        private final Map<String, Container> containers;
        private final Map<Long, Long> serialWithVersions =
                new HashMap<>();

        // the last serial
        private long serial;

        public ContainersHolder() {
            this.containers = new HashMap<>();
        }

        void put(Container container) {
            serial = Math.max(container.getIdentity().serial(), serial);
            containers.put(container.getResourceLocator(), container);
            updateSerialVersion(
                    container.getIdentity().serial(),
                    container.getIdentity().version());
        }

        private void updateSerialVersion(long serial, long version) {
            if (!serialWithVersions.containsKey(serial)) {
                serialWithVersions.put(serial, version);
            }
            long newest = Math.max(serialWithVersions.get(serial), version);
            serialWithVersions.put(serial, newest);
        }

        long lastSerial() {
            return serial;
        }

        long latestVersion(long serial) {
            if (!serialWithVersions.containsKey(serial)) {
                return -1;
            }
            return serialWithVersions.get(serial);
        }

        List<Long> availableVersions(long serial) {
            // TODO: available versions of serial
            return List.of();
        }

        Container latestAvailableContainer() {
            for (Container container : containers.values()) {
                if (check(container, serial, latestVersion(serial))) {
                    return container;
                }
            }
            // allocate a new container
            return null;
        }

        private boolean check(Container container, long serial, long version) {
            if (container.isReachLimit()) {
                return false;
            }
            return container.getIdentity().serial() == serial &&
                    container.getIdentity().version() == version;
        }
    }

}
