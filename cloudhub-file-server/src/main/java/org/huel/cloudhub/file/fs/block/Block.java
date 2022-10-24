package org.huel.cloudhub.file.fs.block;

/**
 * @author RollW
 */
public class Block {
    private byte[] chunk;
    private int validBytes;

    public Block(byte[] chunk, int validBytes) {
        this.chunk = chunk;
        this.validBytes = validBytes;
    }

    public byte[] getChunk() {
        if (chunk == null) {
            throw new IllegalStateException("block already release.");
        }
        return chunk;
    }

    public int getValidBytes() {
        return validBytes;
    }

    public void release() {
        chunk = null;
        validBytes = 0;
    }
}
