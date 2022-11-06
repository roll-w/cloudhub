package org.huel.cloudhub.meta.server.service.file;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import org.huel.cloudhub.file.io.HasherOutputStream;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class ReopenableInputStream extends FilterInputStream {
    private final File file;
    private final Map<String, HashCode> hashCodeMap = new HashMap<>();
    private final long length;

    public ReopenableInputStream(InputStream in, File tempFile, Hasher... hashers) throws IOException {
        super(in);
        this.file = tempFile;
        this.length = initial(hashers);
    }

    private long initial(Hasher... hashers) throws IOException {
        file.createNewFile();
        HasherOutputStream outputStream = new HasherOutputStream(new FileOutputStream(file, false));
        for (Hasher hasher : hashers) {
            outputStream.addHasher(hasher.getClass().getCanonicalName(), hasher);
        }
        long bytes = copy(in, outputStream);
        in.close();
        for (Hasher hasher : hashers) {
            String key = hasher.getClass().getCanonicalName();
            hashCodeMap.put(key, outputStream.getHash(key));
        }
        outputStream.close();
        in = new FileInputStream(file);
        return bytes;
    }

    public long getLength() {
        return length;
    }

    public void reopen() throws FileNotFoundException {
        if (in != null) {
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }
        in = new FileInputStream(file);
    }

    public HashCode getHash(Hasher hasher) {
        return hashCodeMap.get(hasher.getClass().getCanonicalName());
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset() {
        try {
            in.reset();
        } catch (IOException ignored) {
        }
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public void close() throws IOException {
        super.close();
        file.delete();
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    private static long copy(final InputStream input, final OutputStream output)
            throws IOException {

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
