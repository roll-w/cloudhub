package org.huel.cloudhub.file.io;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@SuppressWarnings({"unused", "UnstableApiUsage", "SpellCheckingInspection"})
public class ReopenableInputStream extends FilterInputStream {
    private final File file;
    private final Map<String, Hasher> hashCodeMap = new HashMap<>();
    private final long length;
    private final boolean isTheFile;

    public ReopenableInputStream(InputStream in, File tempFile, Hasher... hashers) throws IOException {
        this(in, tempFile, false, hashers);
    }

    public ReopenableInputStream(InputStream in, File tempFile, boolean isTheFile, Hasher... hashers) throws IOException {
        super(in);
        this.isTheFile = isTheFile;
        this.file = tempFile;
        this.length = initial(hashers);
    }

    private long initial(Hasher... hashers) throws IOException {
        registerHashers(hashers);
        if (in instanceof FileInputStream && isTheFile) {
            in.close();
            in = new FileInputStream(file);
            HasherOutputStream stream = new HasherOutputStream(new NullOutputStream());
            addHashers(stream, hashers);
            long len = ByteStreams.copy(in, stream);
            in.close();
            in = new FileInputStream(file);
            return len;
        }
        file.createNewFile();
        HasherOutputStream outputStream = new HasherOutputStream(new FileOutputStream(file, false));
        addHashers(outputStream, hashers);
        long len = copy(in, outputStream);
        outputStream.close();
        in = new FileInputStream(file);
        return len;
    }

    private void addHashers(HasherStream stream, Hasher... hashers) {
        for (Hasher hasher : hashers) {
            stream.addHasher(hasher.getClass().getCanonicalName(), hasher);
        }
    }

    private void registerHashers(Hasher... hashers) {
        for (Hasher hasher : hashers) {
            hashCodeMap.put(hasher.getClass().getCanonicalName(), hasher);
        }
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
        return hashCodeMap.get(hasher.getClass().getCanonicalName()).hash();
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
        hashCodeMap.clear();
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
            output.flush();
            count += n;
        }
        return count;
    }

    private static long readFull(final InputStream input)
            throws IOException {

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            count += n;
        }
        return count;
    }

    private static class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
        }

        @Override
        public void write(byte[] b) throws IOException {
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
