package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContainerMeta extends ContainerLocator {
    @Override
    String getLocator();

    int getBlockSize();

    int getUsedBlock();

    int getBlockCapacity();

    String getChecksum();

    @Override
    long getVersion();

    @Override
    String getSource();

    ContainerType getContainerType();

    List<? extends BlockFileMeta> getBlockFileMetas();

    void writeTo(OutputStream outputStream) throws IOException;
}
