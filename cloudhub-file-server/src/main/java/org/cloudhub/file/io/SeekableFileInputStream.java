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

package org.cloudhub.file.io;

import java.io.*;

/**
 * 对{@link RandomAccessFile}的封装
 *
 * @author RollW
 */
public class SeekableFileInputStream extends SeekableInputStream implements Seekable {
    private final RandomAccessFile randomAccessFile;

    public SeekableFileInputStream(RepresentFile representFile) throws FileNotFoundException {
        this.randomAccessFile =
                new RandomAccessFile(representFile.toFile(), "rw");
    }

    @Override
    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public long position() throws IOException {
        return randomAccessFile.getFilePointer();
    }

    @Override
    public int read() throws IOException {
        return randomAccessFile.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return randomAccessFile.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return randomAccessFile.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return randomAccessFile.skipBytes((int) n);
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
