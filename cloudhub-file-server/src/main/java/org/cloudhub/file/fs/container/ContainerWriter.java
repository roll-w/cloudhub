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
import org.cloudhub.file.fs.block.Block;
import org.cloudhub.file.io.LimitedSeekableOutputStream;
import org.cloudhub.file.io.SeekableOutputStream;
import org.cloudhub.file.fs.LockException;
import org.cloudhub.file.fs.block.Block;
import org.cloudhub.file.io.LimitedSeekableOutputStream;
import org.cloudhub.file.io.SeekableOutputStream;

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
                           ContainerWriterOpener containerWriterOpener)
            throws IOException, LockException {
        this.container = container;
        this.containerWriterOpener = containerWriterOpener;
        this.stream = convert(
                containerWriterOpener.openContainerWrite(container),
                container.getLimitBytes()
        );
        if (stream == null) {
            throw new ContainerException("Container '%s' not exists"
                    .formatted(container.getLocator()));
        }
        this.blockSizeInBytes = container.getIdentity().blockSizeBytes();
    }

    private LimitedSeekableOutputStream convert(SeekableOutputStream stream, long limit) {
        if (stream == null) {
            return null;
        }

        if (stream instanceof LimitedSeekableOutputStream limited) {
            return limited;
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
            throw new IllegalArgumentException("Start index exceeds container's block limit.");
        }
        if (startIndex + blocks.size() - 1 >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("Block size to write exceeds container's block limit.");
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
            throw new IllegalArgumentException("Start index exceeds container's block limit.");
        }
        if (startIndex + len - 1 >= container.getIdentity().blockLimit()) {
            throw new IllegalArgumentException("Block size to write exceeds container's block limit.");
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
