package org.huel.cloudhub.file.server.service.container;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.protobuf.InvalidProtocolBufferException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.fs.FileAllocator;
import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.file.fs.ServerFile;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.container.*;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerDeleter;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerFinder;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerLoader;
import org.huel.cloudhub.file.fs.container.replica.ReplicaContainerNameMeta;
import org.huel.cloudhub.file.fs.meta.*;
import org.huel.cloudhub.file.server.service.container.event.OnCheckContainerFailureEvent;
import org.huel.cloudhub.server.rpc.status.SerializedContainerType;
import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.huel.cloudhub.util.math.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author RollW
 */
@Service
public class ContainerService implements ContainerAllocator,
        ContainerFinder, ContainerDeleter {
    private final Cache<String, ContainerGroup> containerCache =
            Caffeine.newBuilder()
                    .build();
    private final ContainerProperties containerProperties;
    private final ContainerChecker containerChecker;
    private final ReplicaContainerLoader replicaContainerLoader;
    private final ReplicaContainerFinder replicaContainerFinder;
    private final ReplicaContainerDeleter replicaContainerDeleter;
    private final LocalFileServer localFileServer;
    private final ApplicationEventPublisher eventPublisher;
    private final Logger logger = LoggerFactory.getLogger(ContainerService.class);
    private final ServerFile containerDir;

    public ContainerService(ContainerProperties containerProperties,
                            ContainerChecker containerChecker,
                            ReplicaContainerLoader replicaContainerLoader,
                            ReplicaContainerFinder replicaContainerFinder,
                            ReplicaContainerDeleter replicaContainerDeleter,
                            LocalFileServer localFileServer,
                            ApplicationEventPublisher eventPublisher) throws IOException {
        this.containerProperties = containerProperties;
        this.containerChecker = containerChecker;
        this.replicaContainerLoader = replicaContainerLoader;
        this.replicaContainerFinder = replicaContainerFinder;
        this.replicaContainerDeleter = replicaContainerDeleter;
        this.localFileServer = localFileServer;
        this.eventPublisher = eventPublisher;
        this.containerDir =
                localFileServer.getServerFileProvider().openFile(containerProperties.getContainerPath());
        loadContainers();
    }

    private void loadContainers() throws IOException {
        ServerFile metaDir =
                localFileServer.getServerFileProvider().openFile(containerProperties.getMetaPath());
        metaDir.mkdirs();
        containerDir.mkdirs();
        Set<String> damagedContainerLocators = new HashSet<>();
        Set<String> healthyContainerLocators = new HashSet<>();

        List<ServerFile> metaFiles = metaDir.listFiles();
        List<SerializedContainerMeta> serializedContainerMetas = new ArrayList<>();
        for (ServerFile metaFile : metaFiles) {
            final String fileName = metaFile.getName();
            if (!ContainerMetaKeys.isMetaFile(fileName)) {
                continue;
            }
            if (ContainerMetaKeys.isReplicaMetaFile(fileName)) {
                SerializedReplicaContainerGroupMeta fileMeta =
                        MetaReadWriteHelper.readReplicaContainerMeta(metaFile);
                replicaContainerLoader.loadInReplicaContainers(fileMeta);
                continue;
            }
            SerializedContainerGroupMeta fileMeta =
                    MetaReadWriteHelper.readContainerMeta(metaFile);
            serializedContainerMetas.addAll(fileMeta.getMetaList());
        }

        for (SerializedContainerMeta serializedContainerMeta : serializedContainerMetas) {
            Container container = null;
            try {
                container = loadInContainer(serializedContainerMeta);
                healthyContainerLocators.add(serializedContainerMeta.getLocator());
            } catch (MetadataLostException e) {
                logger.info("Found meta or container lost of container={}.", e.getContainerLocator());
                damagedContainerLocators.add(e.getContainerLocator());
            }
            updatesContainerInContainerGroup(container);
        }
        checkContainerFiles(healthyContainerLocators, damagedContainerLocators);
    }

    // TODO: move to ContainerChecker
    @Async
    void checkContainerFiles(Set<String> healthyLocators,
                             Set<String> damagedLocators) throws IOException {
        Set<Container> unindexedContainers = new HashSet<>();
        // TODO: check containers
        if (containerDir.exists()) {
            List<ServerFile> files =
                    containerDir.listFiles();
            for (ServerFile file : files) {
                checkContainerState(file, healthyLocators);
            }
        }
    }

    private void checkContainerState(ServerFile file, Set<String> healthyLocators) throws IOException {
        String locator = file.getName();
        if (locator.contains(".")) {
            return;
        }
        if (healthyLocators.contains(locator)) {
            return;
        }
        if (locator.contains("-")) {
            checkContainerStateIfReplica(file, locator);
            return;
        }
        checkContainerStateIfContainer(file, locator);
    }

    private void checkContainerStateIfReplica(ServerFile file, String locator) throws IOException {
        ServerFile metaFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator + ContainerLocation.REPLICA_META_SUFFIX);
        ReplicaContainerNameMeta meta = ReplicaContainerNameMeta.parse(locator);
        ContainerGroup group = findGroup(meta.getId(), meta.getSourceId());
        if (!metaFile.exists()) {
            OnCheckContainerFailureEvent event = createExistList(
                    group, meta.getId(), SerializedContainerType.REPLICA);
            eventPublisher.publishEvent(event);
            return;
        }
        try {
            SerializedContainerBlockMeta containerBlockMeta =
                    MetaReadWriteHelper.readContainerBlockMeta(metaFile);
        } catch (IOException e) {
            OnCheckContainerFailureEvent event = createExistList(
                    group, meta.getId(), SerializedContainerType.REPLICA);
            eventPublisher.publishEvent(event);
            return;
        }
    }

    private void checkContainerStateIfContainer(ServerFile containerFile, String locator) throws IOException {
        ServerFile metaFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator + ContainerLocation.META_SUFFIX);
        ContainerNameMeta meta = ContainerNameMeta.parse(locator);
        ContainerGroup group = findGroup(meta.getId(), ContainerFinder.LOCAL);
        if (!metaFile.exists()) {
            // index lost, meta lost. cannot get container info anymore.
            OnCheckContainerFailureEvent event = createExistList(
                    group, meta.getId(), SerializedContainerType.CONTAINER);
            eventPublisher.publishEvent(event);
            return;
        }
        // try parse meta
        try {
            SerializedContainerBlockMeta containerBlockMeta =
                    MetaReadWriteHelper.readContainerBlockMeta(metaFile);

            String crc = containerChecker.calculateChecksum(containerFile);
            String savedCrc = containerBlockMeta.getCrc();
            if (!Objects.equals(crc, savedCrc)) {
                // we don't know which one is damaged, so it's unreliable
                OnCheckContainerFailureEvent event = createExistList(group, meta.getId(),
                        SerializedContainerType.CONTAINER);
                eventPublisher.publishEvent(event);
                return;
            }
        } catch (IOException e) {
            // meta damaged.
            OnCheckContainerFailureEvent event = createExistList(group, meta.getId(),
                    SerializedContainerType.CONTAINER);
            eventPublisher.publishEvent(event);
            return;
        }
        // healthy container
        SerializedContainerMeta serializedContainerMeta =
                SerializedContainerMeta.newBuilder()
                        .setVersion(0)
                        .setLocator(locator)
                        .build();
        // needs to get version from synchro process
    }

    private OnCheckContainerFailureEvent createExistList(ContainerGroup containerGroup,
                                                         String containerId,
                                                         SerializedContainerType serializedContainerType) {
        if (containerGroup == null) {
            SerializedDamagedContainerReport report = SerializedDamagedContainerReport.newBuilder()
                    .setContainerId(containerId)
                    .setType(serializedContainerType)
                    .build();
            return new OnCheckContainerFailureEvent(report);
        }

        List<String> avaFiles = new ArrayList<>(containerGroup.getFileIds());
        SerializedDamagedContainerReport report = SerializedDamagedContainerReport.newBuilder()
                .addAllAvaFileId(avaFiles)
                .setType(serializedContainerType)
                .build();
        return new OnCheckContainerFailureEvent(report);
    }

    private OnCheckContainerFailureEvent createDamagedList(ContainerGroup containerGroup,
                                                           long serial,
                                                           SerializedContainerType serializedContainerType) {
        // container missed, but meta info is available.
        List<String> damFiles = new ArrayList<>();
        SerializedDamagedContainerReport report = SerializedDamagedContainerReport.newBuilder()
                .addAllDamFileId(damFiles)
                .setType(serializedContainerType)
                .build();
        return new OnCheckContainerFailureEvent(report);
    }

    private Container loadInContainer(SerializedContainerMeta serializedContainerMeta) throws IOException, MetadataLostException {
        final String locator = serializedContainerMeta.getLocator();
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator);
        if (!file.exists()) {
            throw new MetadataLostException(serializedContainerMeta.getLocator());
        }

        ContainerNameMeta nameMeta = ContainerNameMeta.parse(locator);
        ServerFile metaFile = localFileServer.getServerFileProvider()
                .openFile(containerDir, locator + ContainerLocation.META_SUFFIX);
        if (!metaFile.exists()) {
            throw new MetadataLostException(serializedContainerMeta.getLocator());
        }

        SerializedContainerBlockMeta containerBlockMeta;
        try {
            containerBlockMeta = MetaReadWriteHelper.readContainerBlockMeta(metaFile);
        } catch (InvalidProtocolBufferException e) {
            throw new MetadataLostException(serializedContainerMeta.getLocator());
        }
        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
        containerBlockMeta.getBlockMetasList().forEach(serializeBlockFileMeta ->
                blockMetaInfos.add(BlockMetaInfo.deserialize(
                        serializeBlockFileMeta, nameMeta.getSerial())));

        ContainerIdentity identity = buildIdentityFrom(nameMeta, containerBlockMeta);
        ContainerLocation location =
                new ContainerLocation(file.getPath());

        return new Container(location, containerBlockMeta.getUsedBlock(),
                identity, blockMetaInfos, serializedContainerMeta.getVersion(), true);
    }

    private ContainerIdentity buildIdentityFrom(ContainerNameMeta nameMeta,
                                                SerializedContainerBlockMeta containerBlockMeta) {
        return new ContainerIdentity(nameMeta.getId(), containerBlockMeta.getCrc(),
                nameMeta.getSerial(), containerBlockMeta.getBlockCap(),
                containerBlockMeta.getBlockSize()
        );
    }


    /**
     * Allocates a container.
     *
     * @param id id
     * @return {@link Container}
     */
    @Override
    @NonNull
    public Container allocateNewContainer(final String id) {
        final String containerId = ContainerIdentity.toContainerId(id);
        ContainerGroup containerGroup = containerCache.getIfPresent(containerId);
        if (containerGroup == null) {
            logger.info("Allocate new container, containerGroup null");

            Container container = createsNewContainer(containerId, 1);
            ContainerGroup newGroup = new ContainerGroup(containerId, ContainerFinder.LOCAL, container);

            containerCache.put(containerId, newGroup);
            return container;
        }

        Container container = createsNewContainer(containerId,
                containerGroup.lastSerial() + 1);
        logger.info("Allocate new container, locator={}", container.getLocator());
        containerGroup.put(container);
        // although a new container has been allocated, but it actually not usable.
        return container;
    }

    @Override
    public @NonNull List<Container> allocateContainers(String id, long size) {
        final String containerId = ContainerIdentity.toContainerId(id);
        ContainerGroup containerGroup = containerCache.getIfPresent(containerId);
        final int needBlocks = Maths.ceilDivideReturnsInt(size, containerProperties.getBlockSizeInBytes());

        if (containerGroup == null) {
            // allocate containers starting at 1.
            logger.debug("ContainerGroup null, allocates from 1.");
            final int needContainers = Maths.ceilDivide(needBlocks, containerProperties.getBlockCount());
            List<Container> containers = allocateContainersFrom(
                    containerId, 1L, needContainers);

            ContainerGroup newGroup =
                    new ContainerGroup(containerId, ContainerFinder.LOCAL, containers);
            containerCache.put(containerId, newGroup);
            return containers;
        }
        List<Container> writableContainers = containerGroup.writableContainers();

        List<Container> res = new ArrayList<>();
        int remainBlockSize = needBlocks;
        for (Container writableContainer : writableContainers) {
            int free = writableContainer.getFreeBlocksCount();
            res.add(writableContainer);
            remainBlockSize -= free;
            if (remainBlockSize <= 0) {
                break;
            }
        }

        final int stillNeedContainers = Maths.ceilDivide(remainBlockSize,
                containerProperties.getBlockCount());
        List<Container> more = allocateContainersFrom(containerId, containerGroup.lastSerial(), stillNeedContainers);
        res.addAll(more);
        return res;
    }

    private List<Container> allocateContainersFrom(String containerId, long start, long size) {
        if (size <= 0) {
            return List.of();
        }
        List<Container> containers = new ArrayList<>();
        for (long i = start; i < start + size; i++) {
            Container container = createsNewContainer(containerId, i);
            containers.add(container);
        }
        return containers;
    }

    @Override
    public boolean dataExists(final String fileId, final String source) {
        final String containerId = ContainerIdentity.toContainerId(fileId);
        ContainerGroup containerGroup = containerCache.getIfPresent(containerId);
        if (containerGroup == null) {
            return false;
        }
        return containerGroup.hasFile(fileId);
    }

    @Override
    public void createsContainerFileWithMeta(Container container) throws IOException {
        if (container.isUsable()) {
            return;
        }
        logger.info("Creates container, locator: {}, ", container.getLocator());
        ServerFile containerFile = localFileServer.getServerFileProvider().openFile(containerProperties.getContainerPath(),
                container.getLocator());
        ServerFile containerMetaFile = localFileServer.getServerFileProvider().openFile(containerProperties.getContainerPath(),
                container.getLocator() + ContainerLocation.META_SUFFIX);
        ServerFile metaFile = localFileServer.getServerFileProvider().openFile(
                containerProperties.getMetaPath(),
                ContainerIdentity.toCmetaId(container.getLocator()) + ContainerMetaKeys.CONTAINER_META_SUFFIX);
        containerFile.createFile();
        containerMetaFile.createFile();
        metaFile.createFile();

        allocateContainerSize(container);

        SerializedContainerMeta meta = SerializedContainerMeta.newBuilder()
                .setLocator(container.getLocator())
                .setVersion(0)
                .build();
        writeContainerMeta(meta);
        container.setUsable();
    }

    private void allocateContainerSize(Container container) throws IOException {
        try (FileAllocator fileAllocator = new FileAllocator(container.getLocation())) {
            fileAllocator.allocateSize(container.getLimitBytes());
        }
    }

    @Override
    public void updatesContainerMetadata(Container container) throws MetadataException, IOException {
        if (!container.isUsable()) {
            throw new MetadataException("Not valid container.");
        }
        ServerFile containerMetaFile = localFileServer.getServerFileProvider().openFile(containerProperties.getContainerPath(),
                container.getLocator() + ContainerLocation.META_SUFFIX);
        SerializedContainerBlockMeta containerBlockMeta = SerializedContainerBlockMeta.newBuilder()
                .setBlockSize(container.getIdentity().blockSize())
                .setBlockCap(container.getIdentity().blockLimit())
                .setUsedBlock(container.getUsedBlocksCount())
                .addAllBlockMetas(container.getSerializedMetaInfos())
                .setCrc(container.getIdentity().crc())
                .build();

        updatesContainerInContainerGroup(container);
        MetaReadWriteHelper.writeContainerBlockMeta(containerBlockMeta, containerMetaFile);
        updateContainerGroupMeta(container);
    }


    private Container createsNewContainer(String id, long serial) {
        ContainerNameMeta fileNameMeta = new ContainerNameMeta(id, serial);
        ContainerIdentity identity = new ContainerIdentity(
                fileNameMeta.getId(),
                ContainerIdentity.INVALID_CRC,
                fileNameMeta.getSerial(),
                containerProperties.getBlockCount(),
                containerProperties.getBlockSize());

        ContainerLocation location = new ContainerLocation(
                ContainerLocation.toDataPath(containerDir, fileNameMeta.getName())
        );

        return new Container(location, 0, identity, List.of(), 1, false);
    }

    public Collection<Container> listContainers(String id) {
        final String containerId = ContainerIdentity.toContainerId(id);
        ContainerGroup containerGroup = containerCache.getIfPresent(containerId);
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

    private void updatesContainerInContainerGroup(Container container) {
        if (container == null) {
            return;
        }

        ContainerGroup containerGroup =
                containerCache.getIfPresent(container.getIdentity().id());
        if (containerGroup == null) {
            containerGroup = new ContainerGroup(container.getIdentity().id(), ContainerFinder.LOCAL, container);
            containerCache.put(container.getIdentity().id(), containerGroup);
            return;
        }

        containerGroup.put(container);
    }


    private void updateContainerGroupMeta(Container container) throws IOException {
        if (!container.isUsable()) {
            return;
        }

        SerializedContainerMeta meta = SerializedContainerMeta.newBuilder()
                .setLocator(container.getLocator())
                .setVersion(container.getVersion())
                .build();
        writeContainerMeta(meta);
    }

    private void removeContainerGroupMeta(Container container) throws IOException {
        if (!container.isUsable()) {
            return;
        }
        if (container.getContainerType() == ContainerType.REPLICA) {
            replicaContainerDeleter.deleteReplicaContainer(
                    container.getIdentity().id(),
                    container.getSerial(),
                    container.getSource());
            return;
        }
        removeContainerMeta(container.getIdentity().id(), container.getSerial());
    }

    @Async
    void removeContainerMeta(String containerId, long serial) throws IOException {
        String fileName = ContainerIdentity.toCmetaId(containerId);
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getMetaPath(), fileName + ContainerMetaKeys.CONTAINER_META_SUFFIX);
        if (!file.exists()) {
            return;
        }
        // TODO: 重构当前的GroupMeta，过于繁琐、重复
        SerializedContainerGroupMeta containerGroupMeta = MetaReadWriteHelper.readContainerMeta(file);
        List<SerializedContainerMeta> containerMetas = new ArrayList<>(containerGroupMeta.getMetaList());
        String containerLocator = new ContainerNameMeta(containerId, serial).getName();
        containerMetas.removeIf(serializedContainerMeta ->
                serializedContainerMeta.getLocator().equals(containerLocator));
        containerGroupMeta = SerializedContainerGroupMeta.newBuilder()
                .addAllMeta(containerMetas)
                .build();
        MetaReadWriteHelper.writeContainerGroupMeta(containerGroupMeta, file);

        if (file.length() == 0) {
            file.delete();
        }
    }

    @Async
    void writeContainerMeta(SerializedContainerMeta containerMeta) throws IOException {
        ContainerNameMeta fileNameMeta =
                ContainerNameMeta.parse(containerMeta.getLocator());
        String fileName = ContainerIdentity.toCmetaId(fileNameMeta.getId());

        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getMetaPath(), fileName + ContainerMetaKeys.CONTAINER_META_SUFFIX);
        boolean createState = file.createFile();
        SerializedContainerGroupMeta containerGroupMeta = MetaReadWriteHelper.readContainerMeta(file);

        List<SerializedContainerMeta> containerMetas = new ArrayList<>(containerGroupMeta.getMetaList());
        containerMetas.removeIf(serializedContainerMeta ->
                serializedContainerMeta.getLocator().equals(containerMeta.getLocator()));
        containerMetas.add(containerMeta);

        containerGroupMeta = SerializedContainerGroupMeta.newBuilder()
                .addAllMeta(containerMetas)
                .build();

        MetaReadWriteHelper.writeContainerGroupMeta(containerGroupMeta, file);
    }

    private ContainerGroup findGroup(String containerId, String source) {
        String id = ContainerIdentity.toContainerId(containerId);
        if (ContainerFinder.isLocal(source)) {
            return containerCache.getIfPresent(id);
        }
        return replicaContainerFinder.findContainerGroup(id, source);
    }

    @Override
    @Nullable
    public Container findContainer(String containerId, long serial, String source) {
        ContainerGroup group = findGroup(containerId, source);
        if (group == null) {
            return null;
        }
        return group.getContainer(serial);
    }

    @Override
    @Nullable
    public ContainerGroup findContainerGroup(String containerId, String source) {
        return findGroup(containerId, source);
    }

    @Override
    @NonNull
    public List<Container> findContainersByFile(String fileId, String source) {
        ContainerGroup group = findContainerGroupByFile(fileId, source);
        if (group == null) {
            return List.of();
        }
        return group.containersWithFile(fileId);
    }

    @Override
    public ContainerGroup findContainerGroupByFile(String fileId, String source) {
        final String containerId = ContainerIdentity.toContainerId(fileId);
        return findGroup(containerId, source);
    }

    @Override
    public void deleteContainer(String id, long serial, String source) throws IOException {
        if (!ContainerFinder.isLocal(source)) {
            replicaContainerDeleter.deleteReplicaContainer(id, serial, source);
            return;
        }

        ContainerGroup group = findGroup(id, source);
        if (group == null) {
            return;
        }
        Container container = group.getContainer(serial);
        if (container == null) {
            return;
        }
        group.remove(container);
        removeContainer(container);
    }

    @Override
    public void deleteContainer(Container container) throws IOException {
        if (container == null) {
            return;
        }
        deleteContainer(container.getIdentity().id(), container.getSerial(), container.getSource());
    }

    private void removeContainer(Container container) throws IOException {
        ServerFile file = localFileServer.getServerFileProvider().openFile(containerDir,
                container.getLocator());
        ServerFile metaFile = localFileServer.getServerFileProvider().openFile(containerDir,
                container.getLocator() + ContainerLocation.META_SUFFIX);
        removeContainerGroupMeta(container);
        file.delete();
        metaFile.delete();
    }
}
