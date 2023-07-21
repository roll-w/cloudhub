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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import static org.cloudhub.file.io.InternalLimitUtils.*;

/**
 * @author RollW
 */
public class LimitedInputStream extends FilterInputStream {
    private final AtomicLong readBytes = new AtomicLong(0);
    private boolean close = false;
    private final long limit;

    public LimitedInputStream(InputStream in, long limit) {
        super(in);
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        return read1(close, limit, readBytes, in);
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return readN(b, off, len, close, limit, readBytes, in);
    }

    void skipOver() throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }
        int available = available();
        long s = skip(available);
    }

    @Override
    public long skip(long n) throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }
        return skipN(n, limit, readBytes, in);
    }

    @Override
    public void close() throws IOException {
        in.close();
        readBytes.set(0);
        close = true;
    }
}
