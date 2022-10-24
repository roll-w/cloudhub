package org.huel.cloudhub.file.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
public class LimitedOutputStream extends FilterOutputStream {
    private final long limit;
    private final AtomicLong writeBytes = new AtomicLong(0);

    private boolean close = false;

    public LimitedOutputStream(OutputStream out, long limit) {
        super(out);
        this.limit = limit;
    }

    private static final int BUFFER_SIZE = 10240;

    /**
     * 如果没有写入到给出的字节数量，则剩余补字节0并关闭流。
     * <br>
     * 此方法写入性能低。
     */
    public void skipOver() throws IOException {
        if (close) {
            return;
        }
        long write0 = limit - writeBytes.get();
        byte[] bytes0 = new byte[BUFFER_SIZE];
        Arrays.fill(bytes0, (byte) 0);
        int times = (int) Math.ceil(write0 / (double) BUFFER_SIZE);
        for (int i = 0; i < times - 1; i++) {
            write(bytes0);
        }
        int last = (int) (BUFFER_SIZE - (times * BUFFER_SIZE - write0));
        write(bytes0, 0, last);
        close();
    }

    @Override
    public void write(int b) throws IOException {
        if (close) {
            throw new IOException("Stream closed");
        }
        long bytes = writeBytes.get();
        if (bytes >= limit) {
            return;
            // neglect if up to given limit.
        }
        writeBytes.getAndIncrement();
        out.write(b);
    }


    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }

    public long position() {
        return writeBytes.get();
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
        flush();
        writeBytes.set(0);
        close = true;
    }
}
