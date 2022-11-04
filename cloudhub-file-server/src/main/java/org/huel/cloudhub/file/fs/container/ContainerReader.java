package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.io.LimitedSeekableInputStream;
import org.huel.cloudhub.file.io.SeekableInputStream;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class ContainerReader implements Closeable {
    private final Container container;
    private final ContainerReadOpener containerReadOpener;
    private final LimitedSeekableInputStream containerInputStream;

    public ContainerReader(Container container,
                           ContainerReadOpener containerReadOpener) throws IOException, LockException {
        this.container = container;
        this.containerReadOpener = containerReadOpener;
        this.containerInputStream = convert(
                containerReadOpener.openContainerRead(container),
                container.getLimitBytes()
        );
    }

    private LimitedSeekableInputStream convert(SeekableInputStream stream, long limit) throws ContainerException {
        if (stream == null) {
            throw new ContainerException("Container not exists.");
        }

        if (stream instanceof LimitedSeekableInputStream) {
            return (LimitedSeekableInputStream) stream;
        }
        return new LimitedSeekableInputStream(stream, limit);
    }

    public List<ContainerBlock> readBlocks(final int start, final int end) throws IOException {
        if (start >= container.getIdentity().blockLimit() || end >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("Invalid start or end, exceeded container's max.");
        }
        if (end - start < 0) {
            throw new IllegalArgumentException("Invalid range at start=%d, end=%d".formatted(start, end));
        }
        if (end - start == 0) {
            return List.of(readBlock(start));
        }

        List<ContainerBlock> containerBlocks = new ArrayList<>();
        long blockSizeBytes = container.getIdentity().blockSizeBytes();
        containerInputStream.seek(blockSizeBytes * start);
        for (int index = start; index <= end; index++){
            byte[] chuck = new byte[(int) blockSizeBytes];
            int read = containerInputStream.read(chuck);
            if (read == -1) {
                throw new ContainerException("Incorrect termination block in [%d], end=%d."
                        .formatted(index, end));
            }
            final long validBytes = calcValidBytes(index, end, container.getIdentity().blockSizeBytes());
            ContainerBlock containerBlock =
                    new ContainerBlock(container.getLocation(), index, chuck, validBytes);
            containerBlocks.add(containerBlock);
        }

        return containerBlocks;
    }

    private long calcValidBytes(int index, int end, long blockSizeInBytes) {
        if (index != end) {
            return blockSizeInBytes;
        }

        BlockMetaInfo blockMetaInfo = container.getBlockMetaInfo(index);
        if (blockMetaInfo.getBlocksCountAfter(index) <= 0)  {
            return blockMetaInfo.getValidBytes();
        }
        return blockSizeInBytes;
    }

    public ContainerBlock readBlock(int index) throws IOException {
        if (index >= container.getIdentity().blockLimit()) {
            return null;
        }
        byte[] chuck = readAt(index);
        if (chuck == null) {
            return null;
        }
        long validBytes = container.getValidBytes(index);
        return new ContainerBlock(
                container.getLocation(), index, chuck, validBytes);
    }

    private byte[] readAt(int index) throws IOException {
        long blockSizeBytes = container.getIdentity().blockSizeBytes();
        containerInputStream.seek(
                blockSizeBytes * index);
        byte[] chuck = new byte[(int) blockSizeBytes];
        int read = containerInputStream.read(chuck);
        if (read == -1) {
            return null;
        }
        return chuck;
    }

    public Container getContainer() {
        return container;
    }

    @Override
    public void close() {
        containerReadOpener.closeContainerRead(container, containerInputStream);
    }
}
