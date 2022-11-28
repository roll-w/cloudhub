package org.huel.cloudhub.meta.server.test;

import java.io.OutputStream;

/**
 * @author RollW
 */
class NullOutputStream extends OutputStream {

    @Override
    public void write(byte[] b) {
    }

    @Override
    public void write(byte[] b, int off, int len) {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) {

    }

    static final NullOutputStream INSTANCE = new NullOutputStream();
}
