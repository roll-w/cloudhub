package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.FileBlockMetaInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class ContainerGroup {
    private final String sourceId;
    private final String containerId;
    private long blockSizeInBytes = -1;
    private volatile long latestSerial;

    private final Map<String, Container> containers = new ConcurrentHashMap<>();
    private final Map<Long, List<FreeBlockInfo>> serialFreeBlockInfos = new ConcurrentHashMap<>();
    private final Set<String> fileIds = ConcurrentHashMap.newKeySet();

    public ContainerGroup(String containerId, String sourceId) {
        this.containerId = containerId;
        this.sourceId = sourceId;
    }

    public ContainerGroup(String containerId, String sourceId, Collection<Container> containers) {
        this.containerId = containerId;
        this.sourceId = sourceId;
        containers.forEach(this::put);
    }

    public ContainerGroup(String containerId, String sourceId, Container container) {
        this.containerId = containerId;
        this.sourceId = sourceId;
        put(container);
    }

    @SuppressWarnings("all")
    public void put(Container container) {
        final long latestSerial = Math.max(container.getIdentity().serial(), this.latestSerial);
        this.latestSerial = latestSerial;
        containers.put(container.getResourceLocator(), container);
        container.getBlockMetaInfos().forEach(blockMetaInfo ->
                fileIds.add(blockMetaInfo.getFileId()));
        List<FreeBlockInfo> containerFreeBlocks = serialFreeBlockInfos
                .computeIfAbsent(container.getIdentity().serial(),
                        v -> new ArrayList<>());

        containerFreeBlocks.addAll(container.getFreeBlockInfos());
        if (blockSizeInBytes >= 0) {
            return;
        }
        // lazy load
        blockSizeInBytes = container.getIdentity().blockSizeBytes();
    }

    public Collection<Container> containers() {
        return Collections.unmodifiableCollection(containers.values());
    }

    public boolean hasFile(String fileId) {
        return fileIds.contains(fileId);
    }

    public long lastSerial() {
        return latestSerial;
    }

    @Nullable
    public Container getContainer(long serial) {
        ContainerNameMeta meta =
                new ContainerNameMeta(containerId, serial);
        return containers.get(meta.getName());
    }

    public Container latestContainer() {
        return getContainer(latestSerial);
    }

    public List<Container> writableContainers() {
        return containers.values().stream()
                .sorted(Comparator.comparingLong(Container::getSerial))
                .filter(container -> !container.isReachLimit())
                .toList();
    }

    @NonNull
    public List<Container> containersWithFile(String fileId) {
        return containers().stream()
                .filter(container -> container.hasFileId(fileId))
                .sorted(Comparator.comparingLong(container -> container.getIdentity().serial()))
                .toList();
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getContainerId() {
        return containerId;
    }

    public long getBlockSizeInBytes() {
        return blockSizeInBytes;
    }

    public FileBlockMetaInfo getFileBlockMetaInfo(String fileId) {
        List<Container> fileContainers = containersWithFile(fileId);
        if (fileContainers.isEmpty()) {
            return null;
        }

        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
        for (Container fileContainer : fileContainers) {
            BlockMetaInfo blockMetaInfo =
                    fileContainer.getBlockMetaInfoByFile(fileId);
            blockMetaInfos.add(blockMetaInfo);
        }

        BlockMetaInfo last = blockMetaInfos.get(blockMetaInfos.size() - 1);
        return new FileBlockMetaInfo(
                fileId, blockMetaInfos, blockSizeInBytes,
                last.getValidBytes());
    }

}
