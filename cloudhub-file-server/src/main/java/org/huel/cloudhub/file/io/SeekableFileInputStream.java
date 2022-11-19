package org.huel.cloudhub.file.io;

import java.io.*;

/**
 * 对{@link RandomAccessFile}的封装
 *
 * @author RollW
 */
public class SeekableFileInputStream extends SeekableInputStream implements Seekable {
    private final RandomAccessFile randomAccessFile;

    public SeekableFileInputStream(RepresentFile representFile) throws FileNotFoundException {
        this.randomAccessFile =
                new RandomAccessFile(representFile.toFile(), "rw");
    }

    @Override
    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public long position() throws IOException {
        return randomAccessFile.getFilePointer();
    }

    @Override
    public int read() throws IOException {
        return randomAccessFile.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return randomAccessFile.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return randomAccessFile.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return randomAccessFile.skipBytes((int) n);
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
