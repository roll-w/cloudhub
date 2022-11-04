package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.io.LimitedSeekableOutputStream;
import org.huel.cloudhub.file.io.SeekableOutputStream;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Only writes data into container, will not
 * update container's meta info or anything else.
 *
 * @author RollW
 */
public class ContainerWriter implements Closeable {
    private final Container container;
    private final ContainerWriterOpener containerWriterOpener;
    private final long blockSizeInBytes;
    private final LimitedSeekableOutputStream stream;

    public ContainerWriter(Container container,
                           ContainerWriterOpener containerWriterOpener) throws IOException, LockException {
        this.container = container;
        this.containerWriterOpener = containerWriterOpener;
        this.stream = convert(
                containerWriterOpener.openContainerWrite(container),
                container.getLimitBytes()
        );
        if (stream == null) {
            throw new ContainerException("Container '%s' not exists"
                    .formatted(container.getResourceLocator()));
        }
        this.blockSizeInBytes = container.getIdentity().blockSizeBytes();
    }

    private LimitedSeekableOutputStream convert(SeekableOutputStream stream, long limit) {
        if (stream == null) {
            return null;
        }

        if (stream instanceof LimitedSeekableOutputStream) {
            return (LimitedSeekableOutputStream) stream;
        }
        return new LimitedSeekableOutputStream(stream, limit);
    }

    public void seek(int index) throws IOException {
        if (index < 0 || index >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("Illegal seek index " + index);
        }
        stream.seek(index * blockSizeInBytes);
    }

    public void write(List<Block> blocks, boolean release) throws IOException {
        write(blocks, 0, blocks.size(), release);
    }

    /**
     * Write blocks in the container.
     *
     * @param blocks blocks data
     * @param off offset of blocks
     * @param len length of blocks to write
     * @param release whether release bytes
     */
    public void write(List<Block> blocks, int off, int len, boolean release) throws IOException {
        if (off < 0) {
            throw new IllegalArgumentException("Illegal off %d.".formatted(off));
        }
        int i = 0;
        for (Block block : blocks) {
            if (i - off >= len) {
                return;
            }
            if (i < off) {
                i++;
                continue;
            }
            stream.write(block.getChunk(), 0, (int) block.getValidBytes());
            if (release) {
                block.release();
            }
            i++;
        }
    }

    public void writeBlocks(List<Block> blocks, int startIndex, boolean release) throws IOException {
        if (startIndex >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("start index exceeds container's block limit.");
        }
        if (startIndex + blocks.size() - 1 >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("block size to write exceeds container's block limit.");
        }
        stream.seek(startIndex * blockSizeInBytes);
        for (Block block : blocks) {
            stream.write(block.getChunk(), 0, (int) block.getValidBytes());
            if (release) {
                block.release();
            }
        }
    }

    public void writeBlocks(List<Block> blocks, int startIndex, int len, boolean release) throws IOException {
        if (startIndex >= container.getIdentity().blockLimit() ||
                startIndex + len >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("start index exceeds container's block limit.");
        }
        if (startIndex + len - 1 >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("block size to write exceeds container's block limit.");
        }
        stream.seek(startIndex * blockSizeInBytes);
        int i = 0;
        for (Block block : blocks) {
            if (i >= len) {
                return;
            }
            stream.write(block.getChunk(), 0, (int) block.getValidBytes());
            if (release) {
                block.release();
            }
            i++;
        }
    }

    public void writeBlocks(List<Block> blocks, int start) throws IOException {
        writeBlocks(blocks, start, true);
    }

    public Container getContainer() {
        return container;
    }

    @Override
    public void close() throws IOException {
        containerWriterOpener.closeContainerWrite(container, stream);
    }
}
