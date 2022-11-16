package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.fs.block.BlockGroup;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.meta.SerializedBlockFileMeta;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Readonly container, every modification will
 * create a new version of container (not implemented).
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

    private int usedBlock;
    private boolean usable;
    private long version;// start from 0

    private final String source;
    private final ContainerType containerType;

    private final ContainerLocation location;
    private final ContainerIdentity identity;
    private final List<BlockMetaInfo> blockMetaInfos = new ArrayList<>();
    private List<FreeBlockInfo> freeBlockInfos;
    private final ReadWriteLock mLock = new ReentrantReadWriteLock();

    public Container(@NonNull ContainerLocation location,
                     int usedBlock,
                     @NonNull ContainerIdentity identity,
                     @NonNull Collection<BlockMetaInfo> blockMetaInfos,
                     long version, boolean usable) {
        this(location, ContainerFinder.LOCAL,
                usedBlock, identity, blockMetaInfos, version, usable);
    }

    public Container(@NonNull ContainerLocation location,
                     String source,
                     int usedBlock,
                     @NonNull ContainerIdentity identity,
                     @NonNull Collection<BlockMetaInfo> blockMetaInfos,
                     long version, boolean usable) {
        this.usedBlock = usedBlock;
        this.source = source;
        this.containerType = calcContainerType(source);
        this.location = location;
        this.identity = identity;
        this.usable = usable;
        this.blockMetaInfos.addAll(blockMetaInfos);
        this.version = version;
        if (usedBlock < 0) {
            calcUsedBlocks(this.blockMetaInfos);
        }
        this.freeBlockInfos = calcFreeBlocks(this.blockMetaInfos);
    }

    public void addBlockMetaInfos(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null) {
            return;
        }
        this.blockMetaInfos.addAll(blockMetaInfos);
        calcBlockUsageInfos(blockMetaInfos);
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
        calcBlockUsageInfos(blockMetaInfos);
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

    private ContainerType calcContainerType(String source) {
        if (source == null || source.equals(ContainerFinder.LOCAL)) {
            return ContainerType.ORIGINAL;
        }
        return ContainerType.REPLICA;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    public String getSource() {
        return source;
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

    public long getSerial() {
        return identity.serial();
    }

    public long getVersion() {
        return version;
    }

    public void updatesVersion() {
        version++;
    }

    public void updatesVersion(long version) {
        if (containerType != ContainerType.REPLICA) {
            return;
        }
        // only works when container is a replica
        this.version = version;
    }

    public int getUsedBlocksCount() {
        return usedBlock;
    }

    public int getFreeBlocksCount() {
        return identity.blockLimit() - usedBlock;
    }

    public boolean isReachLimit() {
        return usedBlock == identity.blockLimit();
    }

    public long getLimitBytes() {
        return identity.blockSizeBytes() * identity.blockLimit();
    }

    public Lock writeLock() {
        return mLock.writeLock();
    }

    public Lock readLock() {
        return mLock.readLock();
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

    private void calcBlockUsageInfos(List<BlockMetaInfo> blockMetaInfos) {
        this.usedBlock = calcUsedBlocks(blockMetaInfos);
        freeBlockInfos = calcFreeBlocks(blockMetaInfos);
    }

    private int calcUsedBlocks(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null || blockMetaInfos.isEmpty()) {
            return 0;
        }
        int usedBlocks = 0;
        final int blocks = blockMetaInfos.stream()
                .mapToInt(BlockMetaInfo::occupiedBlocks).sum();
        usedBlocks += blocks;
        return usedBlocks;
    }

    @NonNull
    private List<FreeBlockInfo> calcFreeBlocks(Collection<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos == null || blockMetaInfos.isEmpty()) {
            return List.of(new FreeBlockInfo(0, getIdentity().blockLimit() - 1));
        }

        List<FreeBlockInfo> freeBlockInfos = new ArrayList<>();
        List<BlockGroup> blockGroups = blockMetaInfos.stream()
                .flatMap(blockMetaInfo -> blockMetaInfo.getBlockGroups().stream())
                .sorted(Comparator.comparingInt(BlockGroup::start))
                .toList();
        Iterator<BlockGroup> blockGroupIterator = blockGroups.listIterator();
        BlockGroup firstGroup = blockGroupIterator.next();
        if (firstGroup == null) {
            return freeBlockInfos;
        }
        addFirstFreeBlock(freeBlockInfos, firstGroup);
        BlockGroup lastGroup = addFreeBlocks(freeBlockInfos,
                firstGroup, blockGroupIterator);

        addLastFreeBlock(freeBlockInfos, lastGroup);
        return freeBlockInfos;
    }

    @NonNull
    private BlockGroup addFreeBlocks(List<FreeBlockInfo> freeBlockInfos,
                                     BlockGroup blockGroup,
                                     Iterator<BlockGroup> blockGroupIterator) {
        if (!blockGroupIterator.hasNext()) {
            return blockGroup;
        }
        BlockGroup next = blockGroupIterator.next();
        FreeBlockInfo freeBlockInfo = new FreeBlockInfo(
                blockGroup.end() + 1, next.start() - 1);
        if (!freeBlockInfo.checkInvalid()) {
            freeBlockInfos.add(freeBlockInfo);
        }
        return addFreeBlocks(freeBlockInfos, next, blockGroupIterator);
    }

    private void addFirstFreeBlock(List<FreeBlockInfo> freeBlockInfos,
                                   BlockGroup blockGroup) {
        if (blockGroup.start() == 0) {
            return;
        }
        FreeBlockInfo freeBlockInfo = new FreeBlockInfo(0, blockGroup.start() - 1);
        if (freeBlockInfo.checkInvalid()) {
            return;
        }
        freeBlockInfos.add(freeBlockInfo);
    }

    private void addLastFreeBlock(List<FreeBlockInfo> freeBlockInfos,
                                  BlockGroup blockGroup) {
        if (blockGroup.end() >= identity.blockLimit() - 1) {
            return;
        }
        FreeBlockInfo freeBlockInfo = new FreeBlockInfo(
                blockGroup.end() + 1, identity.blockLimit() - 1);
        if (freeBlockInfo.checkInvalid()) {
            return;
        }
        freeBlockInfos.add(freeBlockInfo);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return usedBlock == container.usedBlock && usable == container.usable && Objects.equals(location, container.location) && Objects.equals(identity, container.identity) && Objects.equals(blockMetaInfos, container.blockMetaInfos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usedBlock, location, identity, blockMetaInfos, usable);
    }

    @Override
    public String toString() {
        return "Container{" +
                "usedBlock=" + usedBlock +
                ", location=" + location +
                ", identity=" + identity +
                ", blockMetaInfos=" + blockMetaInfos +
                ", valid=" + usable +
                '}';
    }
}
