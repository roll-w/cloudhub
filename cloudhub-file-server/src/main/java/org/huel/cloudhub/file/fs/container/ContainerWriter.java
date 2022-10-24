package org.huel.cloudhub.file.fs.container;

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
import java.util.Collections;
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
        this.validContainer = container.isValid();
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
                BlockMetaInfo metaInfo =
                        collectInfo(containerBlockList, fieldId, blockSize, true);
                mBlockMetaInfos.add(metaInfo);
                blockMetaInfosList.add(metaInfo);
                requireUpdate();
                containerBlockList = new ArrayList<>();
                // needs allocates a new container
                Container newContainer = containerAllocator.allocateContainer(container.getIdentity().id());
                reset(newContainer);
                containerBlock = writeBlock(block);
            }

            containerBlockList.add(containerBlock);
        }

        BlockMetaInfo metaInfo = collectInfo(
                containerBlockList, fieldId, blocks.getValidBytes(), false);
        mBlockMetaInfos.add(metaInfo);
        blockMetaInfosList.add(metaInfo);

        return blockMetaInfosList;
    }

    private BlockMetaInfo collectInfo(List<ContainerBlock> containerBlockList,
                                      String fieldId, int validBytes, boolean cross) {
        int start = containerBlockList.stream().findFirst()
                .get().getIndex();
        Collections.reverse(containerBlockList);
        int end = containerBlockList.stream().findFirst()
                .get().getIndex();
        return new BlockMetaInfo(
                fieldId, start, end,
                validBytes, cross);
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
                containerLocation, index.getAndIncrement(),
                block.getChunk(),
                block.getValidBytes());

        long offset = (long) blockSize * containerBlock.getIndex();
        containerOut.seek(offset);
        containerOut.write(block.getChunk());

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
            allocator.allocateSize(container.calcLimitBytes());
        }
    }

    private void reset(Container container) throws IOException {
        this.container = container;
        validContainer = container.isValid();
        containerLocation = container.getLocation();
        mBlockMetaInfos.clear();
        updates = false;
        initialContainer();
    }

    private SeekableOutputStream openContainer() throws FileNotFoundException {
        return new LimitedSeekableFileOutputStream(container.getLocation(),
                container.calcLimitBytes());
    }

    public Container requireUpdate() throws MetaException, IOException {
        final long newVersion;
        if (validContainer) newVersion= container.getIdentity().version() + 1;
        else newVersion = container.getIdentity().version();

        ContainerIdentity identity =
                new ContainerIdentity(
                        container.getIdentity().id(),
                        // TODO: calc crc32
                        ContainerIdentity.INVALID_CRC,
                        container.getIdentity().serial(),
                        newVersion,
                        container.getIdentity().blockLimit(),
                        container.getIdentity().blockSize());
        ContainerFileNameMeta meta = container.getSimpleMeta()
                .forkVersion(newVersion);
        ContainerLocation updateLocation =
                container.getLocation().fork(meta.toName());
        List<BlockMetaInfo> blockMetaInfos = new ArrayList<>(container.getBlockMetaInfos());
        blockMetaInfos.addAll(mBlockMetaInfos);
        Container updateContainer = new Container(
                updateLocation,
                index.get() - 1,
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
