package org.huel.cloudhub.file.io;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
public class LimitedSeekableOutputStream extends SeekableOutputStream {
    private final AtomicLong writeBytes = new AtomicLong(0);

    private final SeekableOutputStream out;
    private final long limit;
    private boolean close = false;

    public LimitedSeekableOutputStream(SeekableOutputStream out, long limit) {
        this.out = out;
        this.limit = limit;
    }

    @Override
    public void write(int b) throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }
        long bytes = writeBytes.get();
        if (bytes >= limit) {
           return;
        }
        writeBytes.getAndIncrement();
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }
        int readLength = len - off;
        if (readLength > limit) {
            throw new ReachLimitException("reach limit.");
        }
        if (writeBytes.get() + readLength > limit) {
            throw new ReachLimitException("reach limit.");
        }
        writeBytes.addAndGet(readLength);
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (close) {
            return;
        }
        writeBytes.set(0);
        close = true;
    }

    @Override
    public void seek(long position) throws IOException {
        if (position > limit) {
            throw new ReachLimitException("Exceed limit.");
        }
        writeBytes.set(position);
        out.seek(position);
    }

    @Override
    public long position() throws IOException {
        return out.position();
    }

}
