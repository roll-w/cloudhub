package org.huel.cloudhub.file.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 对{@link RandomAccessFile}的封装
 *
 * @author RollW
 */
public class SeekableFileOutputStream extends SeekableOutputStream
        implements Seekable {
    private final RandomAccessFile randomAccessFile;

    public SeekableFileOutputStream(RepresentFile representFile) throws FileNotFoundException {
        this.randomAccessFile =
                new RandomAccessFile(representFile.toFile(), "rw");
    }

    @Override
    public void write(int b) throws IOException {
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        randomAccessFile.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }

    @Override
    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public long position() throws IOException {
        return randomAccessFile.getFilePointer();
    }

}
