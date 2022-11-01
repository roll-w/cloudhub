package org.huel.cloudhub.file.fs.container.file;

import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.fs.container.Container;
import org.huel.cloudhub.file.fs.container.ContainerAllocator;
import org.huel.cloudhub.file.fs.container.ContainerModifier;
import org.huel.cloudhub.file.fs.container.ContainerWriter;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class ContainerFileWriter implements Closeable {
    private final String fileId;
    private final long fileSize;
    private final ContainerAllocator containerAllocator;
    private final ContainerModifier containerModifier;
    private final FileWriteStrategy fileWriteStrategy;
    private List<Container> allowWriteContainers;

    public ContainerFileWriter(String fileId, long fileSize,
                               ContainerAllocator containerAllocator,
                               ContainerModifier containerModifier,
                               FileWriteStrategy fileWriteStrategy) {
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.containerAllocator = containerAllocator;
        this.containerModifier = containerModifier;
        this.fileWriteStrategy = fileWriteStrategy;

        preAllocateContainers();
    }

    private void preAllocateContainers() {
        allowWriteContainers =
                new ArrayList<>(containerAllocator.allocateContainers(fileId, fileSize));
    }

    private Container requireNewContainer() {
        Container container = containerAllocator.allocateNewContainer(fileId);
        allowWriteContainers.add(container);
        return container;
    }

    private record WriteResult(ContainerWriter writer, long serial, int endIndex) {}

    public void writeBlocks(List<Block> blocks) {
        if (fileWriteStrategy == FileWriteStrategy.SEQUENCE) {
            writeBlocksSequence(blocks);
            return;
        }

        long blocksSizeInBytes = calcBlocksSize(blocks);

        // TODO:
    }

    private void writeBlocksSequence(List<Block> blocks) {

    }

    private FileWriteStrategy degradeStrategy() {
        return FileWriteStrategy.SEARCH_MAX;
    }

    private long calcBlocksSize(List<Block> blocks) {
        long res = 0;
        for (Block block : blocks) {
            res += block.getValidBytes();
        }
        return res;
    }

    public String getFileId() {
        return fileId;
    }

    public long getFileSize() {
        return fileSize;
    }

    private boolean allowsWrite(Container container) {
        if (fileWriteStrategy == FileWriteStrategy.SEQUENCE) {
            return true;
        }
        // TODO: file write policy
        return true;
    }

    public FileWriteStrategy getFileWritePolicy() {
        return fileWriteStrategy;
    }

    @Override
    public void close() throws IOException {

    }
}
