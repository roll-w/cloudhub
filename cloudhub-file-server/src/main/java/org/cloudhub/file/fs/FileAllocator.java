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

package org.cloudhub.file.fs;

import org.cloudhub.file.io.RepresentFile;
import org.cloudhub.file.io.RepresentFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件分配
 *
 * @author RollW
 */
public class FileAllocator implements Closeable {
    private final FileChannel fileChannel;
    private final RandomAccessFile accessFile;

    public FileAllocator(RepresentFile file) throws IOException {
        this.accessFile =
                new RandomAccessFile(file.toFile(), "rw");
        this.fileChannel = accessFile.getChannel();
    }

    private static final ByteBuffer PADDING = ByteBuffer.allocateDirect(1);

    public void allocateSize(long byteSize) throws IOException {
        fileChannel.write(PADDING.position(0), byteSize);
    }

    @Override
    public void close() throws IOException {
        fileChannel.close();
        accessFile.close();
    }
}
