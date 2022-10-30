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
    private final String containerId;
    private long blockSizeInBytes = -1;
    private volatile long latestSerial;

    private final Map<String, Container> containers = new ConcurrentHashMap<>();
    private final Map<Long, List<FreeBlockInfo>> serialFreeBlockInfos = new ConcurrentHashMap<>();
    private final Set<String> fileIds = ConcurrentHashMap.newKeySet();

    public ContainerGroup(String containerId) {
        this.containerId = containerId;
    }

    public ContainerGroup(String containerId, Collection<Container> containers) {
        this.containerId = containerId;
        containers.forEach(this::put);
    }

    public ContainerGroup(String containerId, Container container) {
        this.containerId = containerId;
        put(container);
    }

    public void put(Container container) {
        latestSerial = Math.max(container.getIdentity().serial(), latestSerial);
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

    @NonNull
    public List<Container> containersWithFile(String fileId) {
        return containers().stream()
                .filter(container -> container.hasFileId(fileId))
                .toList();
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
