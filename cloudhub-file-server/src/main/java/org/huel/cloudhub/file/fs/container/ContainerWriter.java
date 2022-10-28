package org.huel.cloudhub.file.fs.container;

import com.google.common.collect.Iterables;
import org.huel.cloudhub.file.fs.FileAllocator;
import org.huel.cloudhub.file.fs.block.Block;
import org.huel.cloudhub.file.fs.block.BlockMetaInfo;
import org.huel.cloudhub.file.fs.block.Blocks;
import org.huel.cloudhub.file.fs.block.ContainerBlock;
import org.huel.cloudhub.file.fs.meta.MetaException;
import org.huel.cloudhub.file.io.ClosedStreamException;
import org.huel.cloudhub.file.io.LimitedSeekableFileOutputStream;
import org.huel.cloudhub.file.io.SeekableOutputStream;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Container writer.
 *
 * @author RollW
 */
public class ContainerWriter implements Closeable {
    private Container container;
    private ContainerLocation containerLocation;
    private final int blockSize;

    private final AtomicInteger index = new AtomicInteger(0);

    private SeekableOutputStream containerOut;
    private ContainerAllocator containerAllocator;

    private boolean updates = true;
    private boolean validContainer;

    private final List<BlockMetaInfo> mBlockMetaInfos =
            new LinkedList<>();


    public ContainerWriter(Container container,
                           ContainerAllocator containerAllocator) throws IOException {
        this.container = container;
        this.blockSize = container.getIdentity().blockSize();
        this.containerAllocator = containerAllocator;
        this.containerLocation = container.getLocation();
        this.validContainer = container.isUsable();
        initialContainer();
    }

    private void initialContainer() throws IOException {
        if (validContainer) {
            index.set(container.getUsedBlock() + 1);
            containerOut = openContainer();
            return;
        }
        index.set(0);
        containerAllocator.createsContainerFileWithMeta(container);
        allocateSize();
        containerOut = openContainer();
    }

    public List<BlockMetaInfo> writeBlocks(Blocks blocks) throws IOException, MetaException {
        final String fieldId = blocks.getFileId();
        List<ContainerBlock> containerBlockList = new ArrayList<>();
        List<BlockMetaInfo> blockMetaInfosList = new ArrayList<>();
        for (Block block : blocks.getBlocks()) {
            ContainerBlock containerBlock = writeBlock(block);
            if (containerBlock == null) {
                Container newContainer = containerAllocator.allocateContainer(container.getIdentity().id());
                BlockMetaInfo metaInfo =
                        collectInfo(containerBlockList, fieldId, blockSize,
                                newContainer.getIdentity().serial());
                mBlockMetaInfos.add(metaInfo);
                blockMetaInfosList.add(metaInfo);
                requireUpdate();
                containerBlockList = new ArrayList<>();
                // needs allocates a new container
                reset(newContainer);

                containerBlock = writeBlock(block);
                // it will be null now.
            }

            containerBlockList.add(containerBlock);
            containerBlock.release();
        }

        BlockMetaInfo metaInfo = collectInfo(containerBlockList,
                fieldId, blocks.getValidBytes(),
                BlockMetaInfo.NOT_CROSS_CONTAINER);
        mBlockMetaInfos.add(metaInfo);
        blockMetaInfosList.add(metaInfo);

        return blockMetaInfosList;
    }

    private BlockMetaInfo collectInfo(List<ContainerBlock> containerBlockList,
                                      String fieldId, long validBytes,
                                      long nextSerial) {
        ContainerBlock firstBlock =
                Iterables.getFirst(containerBlockList, null);
        int start = firstBlock.getIndex();
        ContainerBlock lastBlock =
                Iterables.getLast(containerBlockList, null);
        int end = lastBlock.getIndex();

        return new BlockMetaInfo(
                fieldId, start, end,
                validBytes, nextSerial);
    }

    private ContainerBlock writeBlock(Block block) throws IOException {
        if (container == null) {
            throw new ClosedStreamException("container output stream closed.");
        }
        updates = false;
        if (index.get() >= container.getIdentity().blockLimit()) {
            return null;
        }

        ContainerBlock containerBlock = new ContainerBlock(
                containerLocation, index.get(),
                block.getChunk(),
                block.getValidBytes());

        long offset = (long) blockSize * containerBlock.getIndex();
        containerOut.seek(offset);
        containerOut.write(block.getChunk());
        index.incrementAndGet();

        block.release();
        return containerBlock;
    }

    /**
     * 首次创建容器。
     */
    private void allocateSize() throws IOException {
        if (validContainer) {
            return;
        }
        try (FileAllocator allocator = new FileAllocator(container.getLocation().toFile())) {
            allocator.allocateSize(container.getLimitBytes());
        }
    }

    private void reset(Container container) throws IOException {
        this.container = container;
        validContainer = container.isUsable();
        containerLocation = container.getLocation();
        mBlockMetaInfos.clear();
        updates = false;
        initialContainer();
    }

    private SeekableOutputStream openContainer() throws FileNotFoundException {
        return new LimitedSeekableFileOutputStream(container.getLocation(),
                container.getLimitBytes());
    }

    public Container requireUpdate() throws MetaException, IOException {
        ContainerIdentity identity =
                new ContainerIdentity(
                        container.getIdentity().id(),
                        // TODO: calc crc32
                        ContainerIdentity.INVALID_CRC,
                        container.getIdentity().serial(),
                        container.getIdentity().blockLimit(),
                        container.getIdentity().blockSize());
        ContainerNameMeta meta = container.getSimpleMeta();
        ContainerLocation updateLocation =
                container.getLocation().fork(meta.getName());
        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>(container.getBlockMetaInfos());
        blockMetaInfos.addAll(mBlockMetaInfos);
        Container updateContainer = new Container(
                updateLocation,
                index.get(),
                // index is always the next block to write,
                // (index - 1 + 1) equals to (used blocks).
                container.getSimpleMeta(),
                identity,
                blockMetaInfos,
                true);

        containerAllocator.updatesContainerMetadata(updateContainer);
        updates = true;
        return updateContainer;
    }

    @Override
    public void close() throws IOException {
        if (!updates) {
            throw new IllegalStateException("Needs update container meta before.");
        }

        mBlockMetaInfos.clear();
        containerOut.close();
        container = null;
        containerAllocator = null;
    }
}
