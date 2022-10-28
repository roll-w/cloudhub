package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RollW
 */
public class ContainerGroup {
    private final String containerId;
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
        return Objects.requireNonNull(getContainer(latestSerial),
                "Maybe you put a null container in the group.");
    }

}
