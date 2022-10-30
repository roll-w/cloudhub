package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.io.LimitedSeekableStream;
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
    private final ContainerProvider containerProvider;
    private final LimitedSeekableStream containerInputStream;

    public ContainerReader(Container container,
                           ContainerProvider containerProvider) throws IOException {
        this.container = container;
        this.containerProvider = containerProvider;
        this.containerInputStream = convert(
                containerProvider.openContainer(container),
                container.getLimitBytes()
        );
    }

    private LimitedSeekableStream convert(SeekableInputStream stream, long limit) {
        if (stream instanceof LimitedSeekableStream) {
            return (LimitedSeekableStream) stream;
        }
        return new LimitedSeekableStream(stream, limit);
    }

    public List<ContainerBlock> readBlocks(int start, int end) throws IOException {
        if (start >= container.getIdentity().blockLimit() || end >= container.getIdentity().blockLimit()
                || end - start < 0) {
            throw new IllegalArgumentException("Invalid range at start=%d, end=%d".formatted(start, end));
        }
        if (end - start == 0) {
            return List.of(readBlock(start));
        }

        List<ContainerBlock> containerBlocks = new ArrayList<>();
        long blockSizeBytes = container.getIdentity().blockSizeBytes();
        containerInputStream.seek(blockSizeBytes * start);
        for (int i = start; i <= end; i++) {
            byte[] chuck = new byte[(int) blockSizeBytes];
            int read = containerInputStream.read(chuck);
            if (read == -1) {
                throw new ContainerException("Incorrect termination block in [%d], end=%d."
                        .formatted(i, end));
            }
            long validBytes = container.getIdentity().blockSizeBytes();
            if (i == end) {
                BlockMetaInfo blockMetaInfo = container.getBlockMetaInfo(i);
                validBytes = blockMetaInfo.getValidBytes();
            }
            ContainerBlock containerBlock = new ContainerBlock(
                    container.getLocation(), i,
                    chuck, validBytes);
            containerBlocks.add(containerBlock);
        }

        return containerBlocks;
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


    @Override
    public void close() {
        containerInputStream.close();
    }
}
