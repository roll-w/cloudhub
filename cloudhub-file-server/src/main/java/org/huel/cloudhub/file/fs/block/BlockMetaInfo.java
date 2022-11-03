package org.huel.cloudhub.file.fs.block;

import org.huel.cloudhub.file.fs.meta.SerializedBlockFileMeta;
import org.huel.cloudhub.file.fs.meta.SerializedBlockGroup;

import java.util.*;

/**
 * @author RollW
 */
@SuppressWarnings("unused")
public class BlockMetaInfo {
    public static final int NOT_CROSS_CONTAINER = -1;

    private final String fileId;
    private List<BlockGroup> blockGroups = new ArrayList<>();
    private final long containerSerial;
    private final long validBytes;
    private final long nextContainerSerial;

    public BlockMetaInfo(String fileId, int start, int end,
                         long validBytes, long containerSerial,
                         long nextContainerSerial) {
        this.fileId = fileId;
        this.validBytes = validBytes;
        this.containerSerial = containerSerial;
        this.nextContainerSerial = nextContainerSerial;
        this.blockGroups.add(new BlockGroup(start, end));
    }

    public BlockMetaInfo(String fileId, Collection<BlockGroup> blockGroups,
                         long validBytes, long containerSerial, long nextContainerSerial) {
        this.fileId = fileId;
        this.containerSerial = containerSerial;
        this.validBytes = validBytes;
        this.nextContainerSerial = nextContainerSerial;
        this.blockGroups.addAll(blockGroups);
        sort();
    }

    private void sort() {
        blockGroups.sort(Comparator.comparingInt(BlockGroup::start));
        blockGroups = mergeBlockGroups(blockGroups);
        if (!blockGroups.isEmpty()) {
            blockGroups.sort(Comparator.comparingInt(BlockGroup::start));
        }
    }

    public String getFileId() {
        return fileId;
    }

    public List<BlockGroup> getBlockGroups() {
        return Collections.unmodifiableList(blockGroups);
    }

    public long getValidBytes() {
        return validBytes;
    }

    public long getContainerSerial() {
        return containerSerial;
    }

    public long getNextContainerSerial() {
        return nextContainerSerial;
    }

    public boolean isInside(int start, int offset) {
        if (start == -1) {
            return isInside(blockGroupAt(0).start(), offset);
        }

        return getBlocksCountAfter(start) >= offset;
    }

    private List<BlockGroup> mergeBlockGroups(List<BlockGroup> blockGroups) {
        // here the blockGroups have been sorted by start.
        if (blockGroups.isEmpty()) {
            return List.of();
        }
        List<BlockGroup> res = new ArrayList<>();
        final int size = blockGroups.size();
        BlockGroup first = blockGroups.get(0);
        int l = first.start(), r = first.end(), i = 0;
        for (BlockGroup blockGroup : blockGroups) {
            if (i == 0) {
                i++;
                continue;
            }
            if (blockGroup.start() - 1 > r) {
                res.add(new BlockGroup(l, r));
                l = blockGroup.start();
                r = blockGroup.end();
            } else {
                r = Math.max(r, blockGroup.end());
            }
            i++;
        }
        res.add(new BlockGroup(l, r));
        return res;
    }

    public int getBlocksCountAfter(int blockIndex) {
        List<BlockGroup> subs = getBlockGroupAfter(blockIndex);
        if (subs.isEmpty()) {
            return 0;
        }
        int count = 0;

        for (BlockGroup sub : subs) {
            if (blockIndex >= 0 && sub.contains(blockIndex)) {
                count += sub.end() - blockIndex + 1;
                continue;
            }
            count += sub.occupiedBlocks();
        }
        return count;
    }

    public List<BlockGroup> getBlockGroupAfter(int blockIndex) {
        if (blockGroups.isEmpty()) {
            return List.of();
        }
        if (blockIndex > blockGroups.get(blockGroups.size() - 1).end()) {
            return List.of();
        }
        if (blockIndex < 0 || blockIndex <= blockGroups.get(0).start()) {
            return getBlockGroups();
        }

        int index = 0;
        for (BlockGroup blockGroup : blockGroups) {
            if (blockGroup.contains(blockIndex)) {
                break;
            }
            index++;
        }
        // TODO: binary search.
        final int size = blockGroups.size();
        if (size - 1 == index) {
            return List.of(blockGroupAt(size - 1));
        }
        return blockGroups.subList(index, size - 1);
    }

    public int getBlockGroupsCount() {
        return blockGroups.size();
    }

    public BlockGroup blockGroupAt(int index) {
        return blockGroups.get(index);
    }

    public boolean contains(int index) {
        for (BlockGroup blockGroup : blockGroups) {
            if (blockGroup.contains(index)) {
                return true;
            }
        }
        return false;
    }

    public int occupiedBlocks() {
        int sum = 0;
        for (BlockGroup blockGroup : blockGroups) {
            sum += blockGroup.occupiedBlocks();
        }
        return sum;
    }

    public long validBytesAt(int index, long blockSizeInBytes) {
        // because of BlockMeta not hold block size value,
        // so you need pass this "blockSizeInBytes" value.
        BlockGroup lastGroup = blockGroups.get(blockGroups.size() - 1);
        if (lastGroup.end() == index) {
            return validBytes;
        }
        if (contains(index)) {
            return blockSizeInBytes;
        }
        return -1;
    }

    public SerializedBlockFileMeta serialize() {
        List<SerializedBlockGroup> serializedBlockGroups = new ArrayList<>();
        blockGroups.forEach(blockGroup ->
                serializedBlockGroups.add(blockGroup.serialize()));

        return SerializedBlockFileMeta.newBuilder()
                .setFileId(fileId)
                .addAllBlockGroups(serializedBlockGroups)
                .setCrossSerial(nextContainerSerial)
                .setEndBlockBytes(validBytes)
                .build();
    }

    public static BlockMetaInfo deserialize(SerializedBlockFileMeta blockFileMeta, long containerSerial) {
        List<BlockGroup> blockGroups = new ArrayList<>();
        blockFileMeta.getBlockGroupsList().forEach(serializedBlockGroup ->
                blockGroups.add(BlockGroup.deserialize(serializedBlockGroup)));

        return new BlockMetaInfo(blockFileMeta.getFileId(),
                blockGroups,
                blockFileMeta.getEndBlockBytes(),
                containerSerial,
                blockFileMeta.getCrossSerial());
    }

    public BlockMetaInfo forkNextSerial(long nextContainerSerial) {
        return new BlockMetaInfo(fileId,
                blockGroups, validBytes,
                containerSerial, nextContainerSerial);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockMetaInfo that = (BlockMetaInfo) o;
        return containerSerial == that.containerSerial && validBytes == that.validBytes && nextContainerSerial == that.nextContainerSerial && Objects.equals(fileId, that.fileId) && Objects.equals(blockGroups, that.blockGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, blockGroups, containerSerial, validBytes, nextContainerSerial);
    }

    @Override
    public String toString() {
        return "BlockMetaInfo[" +
                "fileId=" + fileId +
                ";blockGroups=" + blockGroups +
                ";validBytes=" + validBytes +
                ";nextContainerSerial=" + nextContainerSerial +
                "]";
    }

}
