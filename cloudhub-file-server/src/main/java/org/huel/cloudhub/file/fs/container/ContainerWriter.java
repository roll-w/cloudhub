package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.Block;
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
    private final ContainerModifier containerModifier;
    private final long blockSizeInBytes;
    private final SeekableOutputStream stream;

    public ContainerWriter(Container container,
                           ContainerModifier containerModifier) throws IOException, LockException {
        this.container = container;
        this.containerModifier = containerModifier;
        this.stream = containerModifier.openContainerWrite(container);
        if (stream == null) {
            throw new ContainerException("Container '%s' not exists"
                    .formatted(container.getResourceLocator()));
        }
        this.blockSizeInBytes = container.getIdentity().blockSizeBytes();
    }

    public void write(List<Block> blocks, int startIndex, boolean release) throws IOException {
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

    public void write(List<Block> blocks, int start) throws IOException {
        write(blocks, start, true);
    }

    @Override
    public void close() throws IOException {
        containerModifier.closeContainerWrite(container, stream);
    }
}
