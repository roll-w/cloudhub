package org.huel.cloudhub.file.fs.container.file;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.fs.block.BlockGroup;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.block.FileBlockMetaInfo;
import org.huel.cloudhub.file.fs.container.*;
import org.huel.cloudhub.file.io.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author RollW
 */
public class ContainerFileReader implements Closeable {
    private final ContainerProvider containerProvider;
    private final ContainerGroup containerGroup;
    private final List<Container> containers;
    private final FileBlockMetaInfo fileBlockMetaInfo;
    private ListIterator<Container> containerIterator;

    private final Logger logger = LoggerFactory.getLogger(ContainerFileReader.class);

    public ContainerFileReader(ContainerProvider containerProvider,
                               String fileId) throws ContainerException {
        this.containerGroup = containerProvider.findContainerGroupByFile(fileId);
        this.containerProvider = containerProvider;
        this.containers = containerGroup.containersWithFile(fileId);
        this.fileBlockMetaInfo = containerGroup.getFileBlockMetaInfo(fileId);

        if (fileBlockMetaInfo == null) {
            throw new ContainerException("no such file");
        }
        initIterator();
    }

    public ContainerFileReader(ContainerProvider containerProvider,
                               String fileId,
                               ContainerGroup containerGroup,
                               FileBlockMetaInfo fileBlockMetaInfo) throws ContainerException {
        this.containerGroup = containerGroup;
        this.containerProvider = containerProvider;
        this.containers = containerGroup.containersWithFile(fileId);
        this.fileBlockMetaInfo = fileBlockMetaInfo;

        if (fileBlockMetaInfo == null) {
            throw new ContainerException("no such file");
        }
        initIterator();
    }

    private void initIterator() {
        containerIterator = containers.listIterator();
    }


    private record ReadBlockDest(long serial, int endIndex) {
    }

    private record ReadResult(List<ContainerBlock> containerBlocks,
                              ContainerReader reader,
                              ReadBlockDest readBlockDest) {
    }

    public boolean hasNext() {
        return containerIterator.hasNext();
    }

    private ReadResult lastResult = null;

    public List<ContainerBlock> read(int readBlocks) throws IOException {
        if (lastResult == null) {
            lastResult = readSizeOf(null, readBlocks, null);
            if (lastResult == null) {
                return null;
            }
            return lastResult.containerBlocks();
        }

        lastResult = readSizeOf(lastResult.reader(), readBlocks, lastResult.readBlockDest());
        if (lastResult == null) {
            return null;
        }
        return lastResult.containerBlocks();
    }

    // 读取给定块数
    private ReadResult readSizeOf(@Nullable ContainerReader reader,
                                  final int readBlockCount,
                                  final ReadBlockDest lastRead) throws IOException {
        ContainerReader containerReader = reader;
        if (reader == null) {
            // first open
            containerReader = openNext(containerIterator);
        }
        if (containerReader == null) {
            return null;
        }

        List<ContainerBlock> containerBlocksRes = new LinkedList<>();
        long serial = 1;
        int start = 0;
        if (lastRead != null) {
            serial = lastRead.serial();
            start = lastRead.endIndex() + 1;
        }

        List<BlockMetaInfo> blockMetaInfos = findBlockCount(fileBlockMetaInfo,
                serial, start, readBlockCount);

        int index = 0, readBlocks = 0;
        final int maxIndex = blockMetaInfos.size() - 1;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (blockMetaInfo.getContainerSerial() != serial) {
                containerReader = openNext(containerIterator);
                serial = containerReader.getContainer().getIdentity().serial();
                start = -1;
                readBlocks = 0;
                logger.info("open next container serial={}.", serial);
            }

            // FIXME: read error here.
            var readResult = readBlocks(containerReader, blockMetaInfo,
                    start, readBlockCount - readBlocks);
            start = readResult.readBlockDest().endIndex() + 1;
            readBlocks += readResult.containerBlocks().size();
            containerBlocksRes.addAll(readResult.containerBlocks());

            if (index != maxIndex) {
                IoUtils.closeQuietly(containerReader);
            } else {
                return new ReadResult(containerBlocksRes, containerReader,
                        new ReadBlockDest(serial, readResult.readBlockDest().endIndex()));
            }

            index++;
        }

        throw new ContainerException("Illegal end at %d, and it should be end at %d in container group '%s'."
                .formatted(index, maxIndex, containerGroup.getContainerId()));
    }

    private ReadResult readBlocks(final ContainerReader reader,
                                  BlockMetaInfo blockMetaInfo,
                                  int start,
                                  int toReadSize) throws IOException {
        // FIXME: read error here

        List<ContainerBlock> containerBlocks = new LinkedList<>();
        int index = 0, sizeRead = 0, endBlock = 0;
        List<BlockGroup> blockGroups = blockMetaInfo.getBlockGroups();
        for (BlockGroup blockGroup : blockGroups) {
            final int startBlock = calcStartBlock(index, start, blockGroup);
            final int willReadBlocks = index == 0
                    ? blockGroup.end() - startBlock + 1
                    : blockGroup.occupiedBlocks();

            if (sizeRead + willReadBlocks >= toReadSize) {
                endBlock = (toReadSize + startBlock - sizeRead) - 1;// -1 to index.
                var read = reader.readBlocks(startBlock, endBlock);
                logger.info("exc: readSize={}, i={}, read into start={}, endBlock={}",
                        read.size(), index, startBlock, endBlock);
                containerBlocks.addAll(read);

                return new ReadResult(containerBlocks, reader,
                        new ReadBlockDest(blockMetaInfo.getContainerSerial(), endBlock));
            }
            // get -p test_d.flac -f e1c60e484e2b49ee1bf405fc937afb57fa02c1581cf0de31ef769ab6ee736934
            endBlock = blockGroup.end();
            List<ContainerBlock> readBlocks =
                    reader.readBlocks(startBlock, blockGroup.end());
            sizeRead += willReadBlocks;
            containerBlocks.addAll(readBlocks);

            index++;
        }

        return new ReadResult(containerBlocks, reader, new ReadBlockDest(
                blockMetaInfo.getContainerSerial(), endBlock));
    }

    private int calcStartBlock(int index, int start, BlockGroup blockGroup) {
        if (index != 0) {
            return blockGroup.start();
        }
        if (start < 0) {
            return blockGroup.start();
        }
        return start;
    }

    private List<BlockMetaInfo> findBlockCount(FileBlockMetaInfo fileBlockMetaInfo,
                                               long startSerial, int start,
                                               int blockCount) {
        if (startSerial <= 1) {
            startSerial = 1;
        }

        List<BlockMetaInfo> blockMetaInfos = fileBlockMetaInfo.getAfter(startSerial);
        List<BlockMetaInfo> res = new ArrayList<>();
        if (blockMetaInfos.isEmpty()) {
            return List.of();
        }

        int needBlocks = blockCount, startBlock = start;
        for (BlockMetaInfo blockMetaInfo : blockMetaInfos) {
            if (needBlocks <= 0) {
                return res;
            }
            final int afterBlocks = blockMetaInfo.getBlocksCountAfter(startBlock);
            if (compareInside(afterBlocks, needBlocks)) {
                res.add(blockMetaInfo);
                return res;
            }
            startBlock = -1;
            needBlocks -= afterBlocks;
            if (afterBlocks > 0) {
                res.add(blockMetaInfo);
            }
        }
        return res;
    }

    private boolean compareInside(int afterBlocks, int offset) {
        return afterBlocks >= offset;
    }

    private ContainerReader openNext(ListIterator<Container> containerIterator) throws IOException {
        if (!containerIterator.hasNext()) {
            return null;
        }

        return new ContainerReader(containerIterator.next(), containerProvider);
    }

    @Override
    public void close() throws IOException {
        if (lastResult == null) {
            return;
        }

        IoUtils.closeQuietly(lastResult.reader());
    }
}