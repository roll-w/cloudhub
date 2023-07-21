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
        int writeLength = len - off;
        if (writeLength > limit) {
            throw new ReachLimitException("reach limit, limit=%d.".formatted(limit));
        }
        if (writeBytes.get() + writeLength > limit) {
            throw new ReachLimitException("reach limit, limit=%d.".formatted(limit));
        }
        writeBytes.addAndGet(writeLength);
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
        out.close();
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
