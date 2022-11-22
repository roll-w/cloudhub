package org.huel.cloudhub.file.io;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class HasherOutputStream extends FilterOutputStream implements HasherStream {
    private final Map<String, Hasher> hashers = new HashMap<>();

    public HasherOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void addHasher(String key, Hasher hasher) {
        if (key == null || hasher == null) {
            return;
        }
        hashers.put(key, hasher);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        hashers.values().forEach(hasher ->
                hasher.putInt(b));
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        hashers.values().forEach(hasher ->
                hasher.putBytes(b));
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        hashers.values().forEach(hasher ->
                hasher.putBytes(b, off, len));
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
        super.close();
    }
}
