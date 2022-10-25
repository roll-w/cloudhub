package org.huel.cloudhub.file.fs.block;

import org.huel.cloudhub.file.fs.meta.SerializeBlockFileMeta;

/**
 * @author RollW
 */
@SuppressWarnings("all")
public class BlockMetaInfo {
    private final String fileId;
    private final int start;
    private final int end;
    private final long validBytes;
    private final boolean crossContainer;

    public BlockMetaInfo(String fileId, int start, int end,
                         long validBytes, boolean crossContainer) {
        this.fileId = fileId;
        this.start = start;
        this.end = end;
        this.validBytes = validBytes;
        this.crossContainer = crossContainer;
    }

    public String getFileId() {
        return fileId;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public long getValidBytes() {
        return validBytes;
    }

    public boolean getCrossContainer() {
        return crossContainer;
    }

    public int occupiedBlocks() {
        return end - start + 1;
    }

    public SerializeBlockFileMeta serialize() {
        return SerializeBlockFileMeta.newBuilder()
                .setFileId(fileId)
                .setStart(start)
                .setEnd(end)
                .setCross(crossContainer)
                .setEndBlockBytes(validBytes)
                .build();
    }

    public static BlockMetaInfo deserialize(SerializeBlockFileMeta blockFileMeta) {
        return new BlockMetaInfo(blockFileMeta.getFileId(),
                blockFileMeta.getStart(),
                blockFileMeta.getEnd(),
                blockFileMeta.getEndBlockBytes(),
                blockFileMeta.getCross());
    }

    @Override
    public String toString() {
        return "BlockMetaInfo[" +
                "fileId=" + fileId +
                ";start=" + start +
                ";end=" + end +
                ";validBytes=" + validBytes +
                ";crossContainer=" + crossContainer +
                "]";
    }
}
