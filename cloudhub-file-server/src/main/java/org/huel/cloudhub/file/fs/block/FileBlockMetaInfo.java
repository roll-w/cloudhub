package org.huel.cloudhub.file.fs.block;

import java.util.Collections;
import java.util.List;

/**
 * @author RollW
 */
public class FileBlockMetaInfo {
    private final String fileId;
    private final long fileLength;
    private final List<BlockMetaInfo> blockMetaInfos;

    public FileBlockMetaInfo(String fileId,
                             List<BlockMetaInfo> blockMetaInfos,
                             long blockSizeInBytes) {
        this.fileId = fileId;
        this.blockMetaInfos = blockMetaInfos;
        this.fileLength = calcFileLength(blockMetaInfos, blockSizeInBytes);
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

    public String getFileId() {
        return fileId;
    }

    public List<BlockMetaInfo> getBlockMetaInfos() {
        return Collections.unmodifiableList(blockMetaInfos);
    }

    public long getFileLength() {
        return fileLength;
    }
}
