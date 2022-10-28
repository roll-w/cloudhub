package org.huel.cloudhub.file.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Seekable, for random read/write use.
 *
 * @author RollW
 */
public interface Seekable extends Closeable {
    void seek(long position) throws IOException;

    long position() throws IOException;
}
