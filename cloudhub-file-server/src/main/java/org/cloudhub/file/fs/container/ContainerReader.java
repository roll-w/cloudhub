/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.fs.container;

import org.cloudhub.file.fs.LockException;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.block.ContainerBlock;
import org.cloudhub.file.io.LimitedSeekableInputStream;
import org.cloudhub.file.io.SeekableInputStream;
import org.cloudhub.file.fs.LockException;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.block.ContainerBlock;
import org.cloudhub.file.io.LimitedSeekableInputStream;
import org.cloudhub.file.io.SeekableInputStream;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author RollW
 */
public class ContainerReader implements Closeable {
    private final Container container;
    private final ContainerReadOpener containerReadOpener;
    private final LimitedSeekableInputStream containerInputStream;
    private final AtomicInteger lastPos = new AtomicInteger(0);

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

        if (stream instanceof LimitedSeekableInputStream limited) {
            return limited;
        }
        return new LimitedSeekableInputStream(stream, limit);
    }

    public void seek(int index) throws IOException {
        if (index < 0) {
            seek(0);
            return;
        }
        if (index == 0) {
            containerInputStream.seek(0);
        } else {
            long blockSizeBytes = container.getIdentity().blockSizeBytes();
            containerInputStream.seek(blockSizeBytes * index);
        }
        lastPos.set(index);
    }

    public List<ContainerBlock> readBlocks(final int count) throws IOException {
        if (count <= 0) {
            return List.of();
        }
        long blockSizeBytes = container.getIdentity().blockSizeBytes();
        List<ContainerBlock> containerBlocks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            byte[] chuck = new byte[(int) blockSizeBytes];
            int read = containerInputStream.read(chuck);
            if (read == -1) {
                throw new ContainerException("Unexpected termination block in [%d], count=%d."
                        .formatted(i, count));
            }
            lastPos.incrementAndGet();
            ContainerBlock containerBlock = new ContainerBlock(
                    container.getLocation(), i, chuck,
                    container.getIdentity().blockSizeBytes());
            containerBlocks.add(containerBlock);
        }
        return containerBlocks;
    }

    public List<ContainerBlock> readBlocks(final int start, final int end) throws IOException {
        if (start >= container.getIdentity().blockLimit() || end >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("Invalid start or end, exceeds container's max.");
        }
        if (end - start < 0) {
            throw new IllegalArgumentException("Invalid range at start=%d, end=%d".formatted(start, end));
        }
        if (end - start == 0) {
            return List.of(readBlock(start));
        }

        List<ContainerBlock> containerBlocks = new ArrayList<>();
        long blockSizeBytes = container.getIdentity().blockSizeBytes();
        if (lastPos.get() != start) {
            seek(start);
        }
        for (int index = start; index <= end; index++){
            byte[] chuck = new byte[(int) blockSizeBytes];
            int read = containerInputStream.read(chuck);
            if (read == -1) {
                throw new ContainerException("Unexpected termination block in [%d], end=%d."
                        .formatted(index, end));
            }
            lastPos.incrementAndGet();
            final long validBytes = calcValidBytes(index, end, container.getIdentity().blockSizeBytes());
            // actually we don't know how many valid bytes here.
            ContainerBlock containerBlock = new ContainerBlock(
                    container.getLocation(), index, chuck, validBytes);
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
        return new ContainerBlock(container.getLocation(),
                index, chuck, validBytes);
    }

    private ContainerBlock readNext() throws IOException {
        // TODO: read next.
        byte[] chuck = readAt(lastPos.get());
        if (chuck == null) {
            return null;
        }
        long validBytes = container.getValidBytes(lastPos.get());
        return new ContainerBlock(
                container.getLocation(), lastPos.get(), chuck, validBytes);
    }

    private byte[] readAt(int index) throws IOException {
        long blockSizeBytes = container.getIdentity().blockSizeBytes();
        if (lastPos.get() != index) {
            seek(index);
        }
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
