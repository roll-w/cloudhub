package org.huel.cloudhub.file.fs.block;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author RollW
 */
public class Blocks {
    private final String fileId;
    private final int validBytes;
    private final List<Block> blocks = new LinkedList<>();

    public Blocks(List<Block> blocks, String fileId, int validBytes) {
        this.fileId = fileId;
        this.validBytes = validBytes;
        this.blocks.addAll(blocks);
    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public String getFileId() {
        return fileId;
    }

    public int getValidBytes() {
        return validBytes;
    }
}
