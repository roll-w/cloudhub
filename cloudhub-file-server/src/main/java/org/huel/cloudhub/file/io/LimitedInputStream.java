package org.huel.cloudhub.file.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
public class LimitedInputStream extends FilterInputStream {
    private final AtomicLong readBytes = new AtomicLong(0);
    private boolean close = false;
    private final int limit;

    public LimitedInputStream(InputStream in, int limit) {
        super(in);
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
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

    void skipOver() throws IOException {
        int available = available();
        long s = skip(available);
    }

    @Override
    public void close() {
        if (close) {
            return;
        }
        readBytes.set(0);
        close = true;
    }
}
