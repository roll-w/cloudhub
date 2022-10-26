package org.huel.cloudhub.meta.server.service.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public class BufferedStreamIterator {
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
