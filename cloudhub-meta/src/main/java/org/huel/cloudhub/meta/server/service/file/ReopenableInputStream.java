package org.huel.cloudhub.meta.server.service.file;

import java.io.*;

/**
 * @author RollW
 */
public class ReopenableInputStream extends FilterInputStream {
    private final File file;
    private final long length;

    public ReopenableInputStream(InputStream in, File tempFile) throws IOException {
        super(in);
        this.file = tempFile;
        this.length = initial();
    }

    private long initial() throws IOException {
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file, false);
        long bytes = copy(in, outputStream);
        in.close();
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
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
