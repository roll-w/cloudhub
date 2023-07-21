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

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class HasherInputStream extends FilterInputStream implements HasherStream {
    private final Map<String, Hasher> hashers = new HashMap<>();

    public HasherInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void addHasher(String key, Hasher hasher) {
        if (key == null || hasher == null) {
            return;
        }
        hashers.put(key, hasher);
    }

    @Override
    public int read() throws IOException {
        int r = in.read();
        if (r == -1) {
            return r;
        }
        hashers.values().forEach(hasher ->
                hasher.putByte((byte) r));
        return r;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int r = in.read(b);

        if (r == -1) {
            return r;
        }
        hashers.values().forEach(hasher ->
                hasher.putBytes(b));
        return r;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = in.read(b, off, len);
        if (r == -1) {
            return r;
        }
        hashers.values().forEach(hasher ->
                hasher.putBytes(b, off, len));
        return r;
    }

    @Override
    public HashCode getHash(String key) {
        if (!hashers.containsKey(key)) {
            return null;
        }
        return hashers.get(key).hash();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
