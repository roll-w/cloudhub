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
