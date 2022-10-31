package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.fs.block.BlockGroup;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.meta.SerializedBlockFileMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Readonly container, every modification will
 * create a new version of container.
 *
 * @author RollW
 */
public class Container {
    /**
     * Pass this flag in constructor {@code usedBlock} param.
     * <p>
     * Marks used block value calculate by the Container.
     */
    public static final int CALC_BYCONT = -1;

    private final AtomicBoolean mLock = new AtomicBoolean(false);

    private int usedBlock;
    private boolean usable;
    private long version;

    private final ContainerLocation location;
    private final ContainerNameMeta meta;
    private final ContainerIdentity identity;
    private final List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
    private final List<FreeBlockInfo> freeBlockInfos = new ArrayList<>();

    public Container(@NonNull ContainerLocation location,
                     int usedBlock,
                     @NonNull ContainerNameMeta meta,
                     @NonNull ContainerIdentity identity,
                     @NonNull Collection<BlockMetaInfo> blockMetaInfos,
                     boolean usable) {
        this.usedBlock = usedBlock;
        this.location = location;
        this.meta = meta;
        this.identity = identity;
        this.usable = usable;
        this.blockMetaInfos.addAll(blockMetaInfos);
        if (usedBlock <= 0) {
            calcUsedBlocks(this.blockMetaInfos);
        }
    }

    public void addBlockMetaInfos(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null) {
            return;
        }
        this.blockMetaInfos.addAll(blockMetaInfos);
        calcUsedBlocks(blockMetaInfos);
    }

    public void addBlockMetaInfos(BlockMetaInfo... blockMetaInfos) {
        addBlockMetaInfos(List.of(blockMetaInfos));
    }

    public void setBlockMetaInfos(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null) {
            return;
        }
        this.blockMetaInfos.clear();
        this.blockMetaInfos.addAll(blockMetaInfos);
        calcUsedBlocks(blockMetaInfos);
    }

    public void setBlockMetaInfos(BlockMetaInfo... blockMetaInfos) {
        setBlockMetaInfos(List.of(blockMetaInfos));
    }

    public List<BlockMetaInfo> getBlockMetaInfos() {
        return Collections.unmodifiableList(blockMetaInfos);
    }

    public List<SerializedBlockFileMeta> getSerializedMetaInfos() {
        List<SerializedBlockFileMeta> serializeBlockFileMetas =
                new ArrayList<>();
        getBlockMetaInfos().forEach(blockMetaInfo ->
                serializeBlockFileMetas.add(blockMetaInfo.serialize()));
        return serializeBlockFileMetas;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable() {
        this.usable = true;
    }

    public boolean isEmpty() {
        return usedBlock <= 0;
    }

    public ContainerLocation getLocation() {
        return location;
    }

    public String getResourceLocator() {
        return location.getResourceLocator();
    }

    public ContainerIdentity getIdentity() {
        return identity;
    }

    public ContainerNameMeta getSimpleMeta() {
        return meta;
    }

    public long getVersion() {
        return version;
    }

    public void updatesVersion() {
        version++;
    }

    public int getUsedBlock() {
        return usedBlock;
    }

    public int getUsableBlock() {
        return identity.blockLimit() - usedBlock;
    }

    public boolean isReachLimit() {
        return usedBlock == identity.blockLimit();
    }

    public long getLimitBytes() {
        return identity.blockSizeBytes() * identity.blockLimit();
    }

    /**
     * Gets the valid bytes count for the block.
     * <p>
     * Returns {@code -1} means means that this block
     * has not yet written any data.
     *
     * @param blockIndex index of the block
     * @return valid bytes count
     */
    public long getValidBytes(int blockIndex) {
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (!blockMetaInfo.contains(blockIndex)) {
                continue;
            }
            return blockMetaInfo.validBytesAt(blockIndex, identity.blockSizeBytes());
        }
        return -1;
    }

    public BlockMetaInfo getBlockMetaInfo(int index) {
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (blockMetaInfo.contains(index)) {
                return blockMetaInfo;
            }
        }
        return null;
    }

    public boolean hasFileId(String fileId) {
        for (BlockMetaInfo blockMetaInfo : this.blockMetaInfos) {
            if (blockMetaInfo.getFileId().equals(fileId)) {
                return true;
            }
        }
        return false;
    }

    public BlockMetaInfo getBlockMetaInfoByFile(String fileId) {
        for (BlockMetaInfo blockMetaInfo : this.blockMetaInfos) {
            if (blockMetaInfo.getFileId().equals(fileId)) {
                return blockMetaInfo;
            }
        }
        return null;
    }

    public List<FreeBlockInfo> getFreeBlockInfos() {
        return Collections.unmodifiableList(freeBlockInfos);
    }

    private void calcUsedBlocks(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null || blockMetaInfos.isEmpty()) {
            return;
        }
        final int blocks = blockMetaInfos.stream()
                .mapToInt(BlockMetaInfo::occupiedBlocks).sum();
        usedBlock += blocks;
    }

    private void calcFreeBlocks() {
        // 计算空闲块
        List<BlockGroup> blockGroups = new ArrayList<>();
        blockMetaInfos.forEach(blockMetaInfo ->
                blockGroups.addAll(blockMetaInfo.getBlockGroups()));
        blockGroups.sort(Comparator.comparingInt(BlockGroup::start));


    }

    public boolean requireLock() {
        return mLock.compareAndSet(false, true);
    }

    public boolean hasLock() {
        return mLock.get();
    }

    public void releaseLock() {
        mLock.set(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return usedBlock == container.usedBlock && usable == container.usable && Objects.equals(location, container.location) && Objects.equals(meta, container.meta) && Objects.equals(identity, container.identity) && Objects.equals(blockMetaInfos, container.blockMetaInfos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usedBlock, location, meta, identity, blockMetaInfos, usable);
    }

    @Override
    public String toString() {
        return "Container{" +
                "usedBlock=" + usedBlock +
                ", location=" + location +
                ", meta=" + meta +
                ", identity=" + identity +
                ", blockMetaInfos=" + blockMetaInfos +
                ", valid=" + usable +
                '}';
    }
}
