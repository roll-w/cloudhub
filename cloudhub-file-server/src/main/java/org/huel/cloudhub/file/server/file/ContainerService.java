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
            Caffeine.newBuilder()
                    .build();
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

            Container container = createsNewContainer(id, 1);
            ContainersHolder newHolder = new ContainersHolder();

            newHolder.put(container);
            containerCache.put(id, newHolder);
            return container;
        }

        Container container = holder.latestAvailableContainer();
        if (container != null) {
            logger.info("find available container: {}", container.getResourceLocator());
            return container;
        }
        container = createsNewContainer(id, holder.lastSerial() + 1);
        logger.info("not find available container, creates new {}", container.getResourceLocator());
        holder.put(container);
        return container;
    }


    @Override
    public boolean dataExists(final String fileId) {
        final String containerId = ContainerIdentity.toMetaId(fileId);
        ContainersHolder holder = containerCache.get(containerId,
                s -> new ContainersHolder());
        if (holder == null) {
            return false;
        }
        return holder.fileIds().contains(fileId);
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

    private Container createsNewContainer(String id, long serial) {
        ContainerFileNameMeta fileNameMeta = new ContainerFileNameMeta(id, serial);
        ContainerIdentity identity = new ContainerIdentity(
                fileNameMeta.id(),
                ContainerIdentity.INVALID_CRC,
                fileNameMeta.serial(),
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
        return Collections.unmodifiableCollection(holder.containers());
    }

    public Collection<Container> listContainers() {
        List<Container> containers = new ArrayList<>();
        for (ContainersHolder value : containerCache.asMap().values()) {
            containers.addAll(value.containers());
        }
        return containers;
    }

    @Async
    void updatesContainer(Container container) {
        ContainersHolder containers =
                containerCache.getIfPresent(container.getResourceLocator());
        if (containers == null) {
            containers = new ContainersHolder();
        }
        containers.put(container);
        containerCache.put(container.getIdentity().id(), containers);
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
        private final Set<String> fileIds;
        // the last serial
        private long serial;

        public ContainersHolder() {
            this.containers = new HashMap<>();
            this.fileIds = new HashSet<>();
        }

        void put(Container container) {
            serial = Math.max(container.getIdentity().serial(), serial);
            containers.put(container.getResourceLocator(), container);
            container.getBlockMetaInfos().forEach(blockMetaInfo ->
                    fileIds.add(blockMetaInfo.getFileId()));
        }

        long lastSerial() {
            return serial;
        }

        Collection<Container> containers() {
            return containers.values();
        }

        Container latestAvailableContainer() {
            for (Container container : containers.values()) {
                if (check(container, serial)) {
                    return container;
                }
            }
            // allocate a new container
            return null;
        }

        public Collection<String> fileIds() {
            return Collections.unmodifiableSet(fileIds);
        }

        private boolean check(Container container, long serial) {
            if (container.isReachLimit()) {
                return false;
            }
            return container.getIdentity().serial() == serial;
        }
    }

}
