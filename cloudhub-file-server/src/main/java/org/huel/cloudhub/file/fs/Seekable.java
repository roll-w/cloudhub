package org.huel.cloudhub.file.fs;

import java.io.IOException;

/**
 * Seekable, for random read/write use.
 *
 * @author RollW
 */
public interface Seekable {
    void seek(long position) throws IOException;
}
