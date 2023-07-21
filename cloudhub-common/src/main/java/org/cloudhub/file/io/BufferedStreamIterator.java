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

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public class BufferedStreamIterator implements Closeable {
    private final InputStream inputStream;
    private final int bufferSize;

    public BufferedStreamIterator(InputStream stream, int bufferSize) {
        this.inputStream = stream;
        this.bufferSize = bufferSize;
    }

    // not recommend
    public BufferedStreamIterator(byte[] bytes, int bufferSize) {
        this(new ByteArrayInputStream(bytes), bufferSize);
    }

    public Buffer nextBuffer() throws IOException {
        byte[] bytesBuffer = new byte[bufferSize];
        int length = inputStream.readNBytes(bytesBuffer, 0, bufferSize);
        if (length < 0) {
            return new Buffer(-1, null, false);
        }
        return new Buffer(length, bytesBuffer, length == bufferSize);
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    public static class Buffer {
        private final long size;
        private final byte[] data;
        private final boolean filled;

        public Buffer(long size, byte[] data, boolean filled) {
            this.size = size;
            this.data = data;
            this.filled = filled;
        }

        public long getSize() {
            return size;
        }

        public byte[] getData() {
            return data;
        }

        public boolean hasData() {
            return size > 0;
        }

        public boolean notFilled() {
            if (size <= 0) {
                return true;
            }
            return !filled;
        }
    }
}
