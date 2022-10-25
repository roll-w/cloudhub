package org.huel.cloudhub.file.fs.block;

/**
 * @author RollW
 */
public class Block {
    private byte[] chunk;
    private long validBytes;

    public Block(byte[] chunk, long validBytes) {
        this.chunk = chunk;
        this.validBytes = validBytes;
    }

    public byte[] getChunk() {
        if (chunk == null) {
            throw new IllegalStateException("block already release.");
        }
        return chunk;
    }

    public long getValidBytes() {
        return validBytes;
    }

    public void release() {
        chunk = null;
        validBytes = 0;
    }
}
