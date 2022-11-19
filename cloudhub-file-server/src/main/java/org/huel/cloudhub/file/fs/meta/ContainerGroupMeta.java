package org.huel.cloudhub.file.fs.meta;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContainerGroupMeta {
    List<? extends ContainerMeta> getMetas();

    void writeTo(OutputStream outputStream) throws IOException;
}
