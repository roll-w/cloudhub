package org.huel.cloudhub.file.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
public class LimitedSeekableFileOutputStream extends SeekableFileOutputStream {
    private final long limit;
    private final AtomicLong writeBytes = new AtomicLong(0);

    public LimitedSeekableFileOutputStream(RepresentFile representFile, long limit)
            throws FileNotFoundException {
        super(representFile);
        this.limit = limit;
    }

    @Override
    public void write(int b) throws IOException {
        if (writeBytes.get() > limit) {
            throw new ReachLimitException("write reach limit.");
        }
        writeBytes.incrementAndGet();
        super.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        int length = b.length;
        if (length > limit) {
            throw new ReachLimitException("write reach limit.");
        }
        if (writeBytes.get() + length > limit) {
            throw new ReachLimitException("write reach limit.");
        }
        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (off + len > limit) {
            throw new ReachLimitException("write reach limit.");
        }

        if (writeBytes.get() + off + len > limit) {
            throw new ReachLimitException("write reach limit.");
        }
        super.write(b, off, len);
    }

    @Override
    public void seek(long position) throws IOException {
        if (position > limit) {
            throw new ReachLimitException("seek to limit, " + limit);
        }
        writeBytes.set(position);
        super.seek(position);
    }
}
