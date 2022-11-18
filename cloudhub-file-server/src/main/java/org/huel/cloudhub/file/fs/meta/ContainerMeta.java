package org.huel.cloudhub.file.fs.meta;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
public interface ContainerMeta {
    String getLocator();

    long getVersion();

    void writeTo(OutputStream outputStream) throws IOException;
}
