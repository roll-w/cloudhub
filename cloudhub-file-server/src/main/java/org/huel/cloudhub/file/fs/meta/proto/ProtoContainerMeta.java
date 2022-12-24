package org.huel.cloudhub.file.fs.meta.proto;

import org.huel.cloudhub.file.fs.meta.ContainerMeta;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
public class ProtoContainerMeta implements ContainerMeta {
    // TODO:
    public ProtoContainerMeta() {

    }

    @Override
    public String getLocator() {
        return null;
    }

    @Override
    public long getVersion() {
        return 0;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {

    }
}
