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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 对{@link RandomAccessFile}的封装
 *
 * @author RollW
 */
public class SeekableFileOutputStream extends SeekableOutputStream
        implements Seekable {
    private final RandomAccessFile randomAccessFile;

    public SeekableFileOutputStream(RepresentFile representFile) throws FileNotFoundException {
        this.randomAccessFile =
                new RandomAccessFile(representFile.toFile(), "rw");
    }

    @Override
    public void write(int b) throws IOException {
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        randomAccessFile.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }

    @Override
    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public long position() throws IOException {
        return randomAccessFile.getFilePointer();
    }

}
