package org.huel.cloudhub.file.fs.block;

import java.util.Comparator;
import java.util.List;

/**
 * @author RollW
 */
public class FileBlockMetaInfo {
    private final String fileId;
    private final long fileLength;
    private final long validBytes;
    private final List<BlockMetaInfo> blockMetaInfos;
    private final long[] serials;

    public FileBlockMetaInfo(String fileId,
                             List<BlockMetaInfo> blockMetaInfos,
                             long blockSizeInBytes, long validBytes) {
        this.fileId = fileId;
        this.blockMetaInfos = sortBlockMetas(blockMetaInfos);
        this.validBytes = validBytes;
        this.fileLength = calcFileLength(blockMetaInfos, blockSizeInBytes);
        this.serials = calcSerials(blockMetaInfos);
    }

    private List<BlockMetaInfo> sortBlockMetas(List<BlockMetaInfo> blockMetaInfos) {
        return blockMetaInfos.stream()
                .sorted(Comparator.comparingLong(BlockMetaInfo::getContainerSerilal))
                .toList();
    }

    private long[] calcSerials(List<BlockMetaInfo> blockMetaInfos) {
        if (blockMetaInfos.isEmpty()) {
            return new long[0];
        }
        long[] serials = new long[blockMetaInfos.size()];
        int index = 0;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            serials[index++] = blockMetaInfo.getContainerSerilal();
        }
        return serials;
    }

    private long calcFileLength(List<BlockMetaInfo> blockMetaInfos, long blockSizeInBytes) {
        long res = 0;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (blockMetaInfo.getNextContainerSerial() == BlockMetaInfo.NOT_CROSS_CONTAINER) {
                res += blockMetaInfo.occupiedBlocks() * blockSizeInBytes;
                continue;
            }
            res += (blockMetaInfo.occupiedBlocks() - 1) * blockSizeInBytes +
                    blockMetaInfo.getValidBytes();
        }
        return res;
    }

    public long getValidBytes() {
        return validBytes;
    }

    public String getFileId() {
        return fileId;
    }

    public List<BlockMetaInfo> getAfter(long serial) {
        return blockMetaInfos.stream()
                .filter(blockMetaInfo ->
                        blockMetaInfo.getContainerSerilal() >= serial)
                .toList();
    }

    public BlockMetaInfo getBlockMetaInfoAt(long serial) {
        // binary search.
        int low = 0;
        int high = blockMetaInfos.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            BlockMetaInfo midVal = blockMetaInfos.get(mid);
            int cmp = Long.compare(midVal.getContainerSerilal(), serial);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return midVal; // found value
            }
        }
        return null;
    }

    public long[] getSerials() {
        return serials;
    }

    public List<BlockMetaInfo> getBlockMetaInfos() {
        return blockMetaInfos;
    }

    public long getFileLength() {
        return fileLength;
    }
}
