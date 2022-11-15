package org.huel.cloudhub.file.io;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class HasherInputStream extends FilterInputStream {
    private final Map<String, Hasher> hashers = new HashMap<>();

    public HasherInputStream(InputStream in) {
        super(in);
    }

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
                hasher.putInt(r));
        return r;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int r = super.read(b);

        if (r == -1) {
            return r;
        }
        hashers.values().forEach(hasher ->
                hasher.putBytes(b));
        return r;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = super.read(b, off, len);
        if (r == -1) {
            return r;
        }
        hashers.values().forEach(hasher ->
                hasher.putBytes(b, off, len));
        return r;
    }


    public HashCode getHash(String key) {
        if (!hashers.containsKey(key)) {
            return null;
        }
        return hashers.get(key).hash();
    }
}
