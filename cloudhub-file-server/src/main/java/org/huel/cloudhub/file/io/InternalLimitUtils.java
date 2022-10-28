package org.huel.cloudhub.file.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
class InternalLimitUtils {
    public static long skipN(long n, long limit, AtomicLong readBytes, InputStream in) throws IOException {
        if (n > limit) {
            return 0;
        }
        if (readBytes.get() + n > limit) {
            return 0;
        }
        long skip = in.skip(n);
        readBytes.addAndGet(skip);
        return skip;
    }

    public static int readN(byte[] b, int off, int len, boolean close, long limit, AtomicLong readBytes, InputStream in) throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }

        int readLength = len - off;
        if (readLength > limit) {
            return -1;
        }
        if (readBytes.get() + readLength > limit) {
            return -1;
        }
        readBytes.addAndGet(readLength);
        return in.read(b, off, len);
    }

    public static int read1(boolean close, long limit, AtomicLong readBytes, InputStream in) throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }

        long bytes = readBytes.get();
        if (bytes >= limit) {
            return -1;
        }
        readBytes.getAndIncrement();
        return in.read();
    }

    private InternalLimitUtils() {
    }
}
