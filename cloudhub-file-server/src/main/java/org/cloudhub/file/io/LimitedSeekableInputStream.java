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

import static org.cloudhub.file.io.InternalLimitUtils.*;

/**
 * @author RollW
 */
public class LimitedSeekableInputStream extends SeekableInputStream
        implements Seekable {
    private final AtomicLong readBytes = new AtomicLong(0);
    private final long limit;
    private boolean close = false;
    private final SeekableInputStream in;
    
    public LimitedSeekableInputStream(SeekableInputStream in, long limit) {
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
        return InternalLimitUtils.read1(close, limit, readBytes, in);
    }


    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return InternalLimitUtils.readN(b, off, len, close, limit, readBytes, in);
    }

    @Override
    public long skip(long n) throws IOException {
        return InternalLimitUtils.skipN(n, limit, readBytes, in);
    }

    @Override
    public void close() throws IOException {
        if (close) {
            return;
        }
        readBytes.set(0);
        in.close();
        close = true;
    }

    @Override
    public int available() {
        return (int) (limit - readBytes.get());
    }
}
