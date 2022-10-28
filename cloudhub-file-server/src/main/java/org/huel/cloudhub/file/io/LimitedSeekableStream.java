package org.huel.cloudhub.file.io;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import static org.huel.cloudhub.file.io.InternalLimitUtils.*;

/**
 * @author RollW
 */
public class LimitedSeekableStream extends SeekableInputStream 
        implements Seekable {
    private final AtomicLong readBytes = new AtomicLong(0);
    private final long limit;
    private boolean close = false;
    private final SeekableInputStream in;
    
    public LimitedSeekableStream(SeekableInputStream in, long limit) {
        this.limit = limit;
        this.in = in;
    }

    @Override
    public void seek(long position) throws IOException {
        if (position > limit) {
            throw new ReachLimitException("Exceed limit.");
        }
        readBytes.set(position);
        in.seek(position);
    }

    @Override
    public long position() throws IOException {
        return in.position();
    }

    @Override
    public int read() throws IOException {
        return read1(close, limit, readBytes, in);
    }


    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return readN(b, off, len, close, limit, readBytes, in);
    }

    @Override
    public long skip(long n) throws IOException {
        return skipN(n, limit, readBytes, in);
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
