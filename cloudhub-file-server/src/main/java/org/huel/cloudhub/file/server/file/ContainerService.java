package org.huel.cloudhub.file.server.file;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.container.*;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.fs.meta.SerializedContainerBlockMeta;
import org.huel.cloudhub.file.fs.meta.SerializedContainerGroupMeta;
import org.huel.cloudhub.file.fs.meta.SerializedContainerMeta;
import org.huel.cloudhub.file.io.SeekableFileInputStream;
import org.huel.cloudhub.file.io.SeekableInputStream;
import org.huel.cloudhub.server.file.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author RollW
 */
@Service
public class ContainerService implements ContainerAllocator, ContainerProvider {
    private final Cache<String, ContainerGroup> containerCache =
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
        loadContainers();
    }

    private void loadContainers() throws IOException {
        ServerFile metaDir =
                localFileServer.getServerFileProvider().openFile(fileProperties.getMetaPath());
        metaDir.mkdirs();
        containerDir.mkdirs();

        List<ServerFile> metaFiles = metaDir.listFiles();
        List<String> locators = new ArrayList<>();
        for (ServerFile metaFile : metaFiles) {
            if (Objects.equals(ContainerAllocator.CONTAINER_META_SUFFIX,
                    FileUtils.getExtensionName(metaFile.getName()))) {
                continue;
            }

            SerializedContainerGroupMeta fileMeta = readContainerMeta(metaFile);
            fileMeta.getMetaList().forEach(meta ->
                    locators.add(meta.getLocator()));
        }

        for (String locator : locators) {
            ServerFile file = localFileServer.getServerFileProvider()
                    .openFile(containerDir, locator);
            ServerFile metaFile = localFileServer.getServerFileProvider()
                    .openFile(containerDir, locator + ContainerLocation.META_SUFFIX);
            SerializedContainerBlockMeta containerBlockMeta = readContainerBlockMeta(metaFile);
            List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
            containerBlockMeta.getBlockMetasList().forEach(serializeBlockFileMeta ->
                    blockMetaInfos.add(BlockMetaInfo.deserialize(serializeBlockFileMeta)));

            ContainerNameMeta fileNameMeta = ContainerNameMeta.parse(locator);
            ContainerIdentity identity = new ContainerIdentity(
                    fileNameMeta.getId(),
                    containerBlockMeta.getCrc(),
                    fileNameMeta.getSerial(),
                    containerBlockMeta.getBlockCap(),
                    containerBlockMeta.getBlockSize());

            ContainerLocation location =
                    new ContainerLocation(file.getPath());
            Container container = new Container(
                    location,
                    containerBlockMeta.getUsedBlock(),
                    fileNameMeta, identity,
                    blockMetaInfos, true);
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
    public Container allocateContainer(final String id) {
        final String containerId = ContainerIdentity.toContainerId(id);
        ContainerGroup containerGroup = containerCache.get(containerId,
                s -> new ContainerGroup(containerId));

        if (containerGroup == null) {
            logger.info("containerGroup null");

            Container container = createsNewContainer(containerId, 1);
            ContainerGroup newGroup = new ContainerGroup(containerId, container);

            containerCache.put(containerId, newGroup);
            return container;
        }

        Container container = containerGroup.latestContainer();
        if (container != null && !container.isReachLimit()) {
            logger.info("find an available container: {}", container.getResourceLocator());
            return container;
        }
        container = createsNewContainer(id, containerGroup.lastSerial() + 1);
        logger.info("not find an available container, creates new {}", container.getResourceLocator());
        containerGroup.put(container);
        return container;
    }

    @Override
    public List<Container> allocateContainers(String id, long size) {
        // TODO:
        return null;
    }

    @Override
    public boolean dataExists(final String fileId) {
        final String containerId = ContainerIdentity.toContainerId(fileId);
        ContainerGroup containerGroup = containerCache.get(containerId,
                s -> new ContainerGroup(containerId));
        if (containerGroup == null) {
            return false;
        }
        return containerGroup.hasFile(fileId);
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
                ContainerIdentity.toCmetaId(container.getResourceLocator()) + CONTAINER_META_SUFFIX);
        containerFile.createFile();
        containerMetaFile.createFile();
        metaFile.createFile();

        var meta =
                SerializedContainerMeta.newBuilder().setLocator(container.getResourceLocator()).build();
        writeContainerMeta(meta);
        container.setUsable();
    }

    @Override
    public void updatesContainerMetadata(Container container) throws MetaException, IOException {
        if (!container.isUsable()) {
            throw new MetaException("Not valid container.");
        }
        ServerFile containerMetaFile = localFileServer.getServerFileProvider().openFile(fileProperties.getContainerPath(),
                container.getResourceLocator() + ContainerLocation.META_SUFFIX);
        SerializedContainerBlockMeta containerBlockMeta = SerializedContainerBlockMeta.newBuilder()
                .setBlockSize(container.getIdentity().blockSize())
                .setBlockCap(container.getIdentity().blockLimit())
                .setUsedBlock(container.getUsedBlock())
                .addAllBlockMetas(container.getSerializedMetaInfos())
                .setCrc(container.getIdentity().crc())
                .build();

        updatesContainer(container);
        containerBlockMeta.writeTo(containerMetaFile.openOutput(true));
    }

    private Container createsNewContainer(String id, long serial) {
        ContainerNameMeta fileNameMeta = new ContainerNameMeta(id, serial);
        ContainerIdentity identity = new ContainerIdentity(
                fileNameMeta.getId(),
                ContainerIdentity.INVALID_CRC,
                fileNameMeta.getSerial(),
                fileProperties.getBlockCount(),
                fileProperties.getBlockSize());

        ContainerLocation location = new ContainerLocation(
                ContainerLocation.toDataPath(containerDir, fileNameMeta.getName())
        );

        return new Container(location,
                0,
                fileNameMeta,
                identity, List.of(), false);
    }

    public Collection<Container> listContainers(String id) {
        final String containerId = ContainerIdentity.toContainerId(id);
        ContainerGroup containerGroup =
                containerCache.get(id, s -> new ContainerGroup(containerId));
        if (containerGroup == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(containerGroup.containers());
    }

    public Collection<Container> listContainers() {
        List<Container> containers = new ArrayList<>();
        for (ContainerGroup value : containerCache.asMap().values()) {
            containers.addAll(value.containers());
        }
        return containers;
    }

    @Async
    void updatesContainer(Container container) {
        ContainerGroup containerGroup =
                containerCache.getIfPresent(container.getResourceLocator());
        if (containerGroup == null) {
            containerGroup = new ContainerGroup(container.getIdentity().id());
        }
        containerGroup.put(container);
        containerCache.put(container.getIdentity().id(), containerGroup);
    }

    private SerializedContainerBlockMeta readContainerBlockMeta(ServerFile file) throws IOException {
        return SerializedContainerBlockMeta.parseFrom(file.openInput());
    }

    @Async
    void writeContainerMeta(SerializedContainerMeta containerMeta) throws IOException {
        ContainerNameMeta fileNameMeta =
                ContainerNameMeta.parse(containerMeta.getLocator());
        String fileName = ContainerIdentity.toCmetaId(fileNameMeta.getId());

        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(fileProperties.getMetaPath(), fileName + CONTAINER_META_SUFFIX);
        boolean createState = file.createFile();
        SerializedContainerGroupMeta containerFileMeta =
                SerializedContainerGroupMeta.parseFrom(file.openInput());
        List<SerializedContainerMeta> containerMetas = new ArrayList<>(containerFileMeta.getMetaList());
        containerMetas.add(containerMeta);
        containerMetas = containerMetas.stream().distinct().toList();
        containerFileMeta = SerializedContainerGroupMeta.newBuilder()
                .addAllMeta(containerMetas)
                .build();
        containerFileMeta.writeTo(
                file.openOutput(true));
    }

    public SerializedContainerGroupMeta readContainerMeta(ServerFile serverFile) throws IOException {
        return SerializedContainerGroupMeta.parseFrom(serverFile.openInput());
    }

    private boolean checkHasContainer(String containerId, long serial) {
        ContainerGroup group = findGroup(containerId);
        if (group == null) {
            return false;
        }
        return serial <= group.lastSerial();
    }

    private boolean checkHasContainer(ContainerLocation location) {
        return location.toFile().exists();
    }

    @Override
    public SeekableInputStream openContainer(ContainerLocation location) throws IOException {
        if (!checkHasContainer(location)) {
            return null;
        }
        return new SeekableFileInputStream(location);
    }

    private ContainerGroup findGroup(String containerId) {
        return containerCache.getIfPresent(containerId);
    }

    @Override
    @Nullable
    public Container findContainer(String containerId, long serial) {
        ContainerGroup group = findGroup(containerId);
        if (group == null) {
            return null;
        }
        return group.getContainer(serial);
    }

    @Override
    @NonNull
    public List<Container> findContainersByFile(String fileId) {
        ContainerGroup group = findContainerGroupByFile(fileId);
        if (group == null) {
            return List.of();
        }
        return group.containersWithFile(fileId);
    }

    @Override
    public ContainerGroup findContainerGroupByFile(String fileId) {
        final String containerId = ContainerIdentity.toContainerId(fileId);
        return findGroup(containerId);
    }
}
