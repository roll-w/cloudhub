package org.huel.cloudhub.file.fs.block;

import org.huel.cloudhub.file.fs.meta.SerializedBlockFileMeta;
import org.huel.cloudhub.file.fs.meta.SerializedBlockGroup;

import java.util.*;

/**
 * @author RollW
 */
@SuppressWarnings("all")
public class BlockMetaInfo {
    public static final int NOT_CROSS_CONTAINER = -1;

    private final String fileId;
    private final List<BlockGroup> blockGroups = new ArrayList<>();
    private final long containerSerilal;
    private final long validBytes;
    private final long nextContainerSerial;

    public BlockMetaInfo(String fileId, int start, int end,
                         long validBytes, long containerSerilal, long nextContainerSerial) {
        this.fileId = fileId;
        this.validBytes = validBytes;
        this.containerSerilal = containerSerilal;
        this.nextContainerSerial = nextContainerSerial;
        this.blockGroups.add(new BlockGroup(start, end));
    }

    public BlockMetaInfo(String fileId, Collection<BlockGroup> blockGroups,
                         long validBytes, long containerSerilal, long nextContainerSerial) {
        this.fileId = fileId;
        this.containerSerilal = containerSerilal;
        this.validBytes = validBytes;
        this.nextContainerSerial = nextContainerSerial;
        this.blockGroups.addAll(blockGroups);
        sort();
    }

    private void sort() {
        blockGroups.sort(Comparator.comparingInt(BlockGroup::start));
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

    public long getContainerSerilal() {
        return containerSerilal;
    }

    public long getNextContainerSerial() {
        return nextContainerSerial;
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

    public static BlockMetaInfo deserialize(SerializedBlockFileMeta blockFileMeta, long containerSerilal) {
        List<BlockGroup> blockGroups = new ArrayList<>();
        blockFileMeta.getBlockGroupsList().forEach(serializedBlockGroup ->
                blockGroups.add(BlockGroup.deserialize(serializedBlockGroup)));

        return new BlockMetaInfo(blockFileMeta.getFileId(),
                blockGroups,
                blockFileMeta.getEndBlockBytes(),
                containerSerilal,
                blockFileMeta.getCrossSerial());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockMetaInfo that = (BlockMetaInfo) o;
        return containerSerilal == that.containerSerilal && validBytes == that.validBytes && nextContainerSerial == that.nextContainerSerial && Objects.equals(fileId, that.fileId) && Objects.equals(blockGroups, that.blockGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, blockGroups, containerSerilal, validBytes, nextContainerSerial);
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
