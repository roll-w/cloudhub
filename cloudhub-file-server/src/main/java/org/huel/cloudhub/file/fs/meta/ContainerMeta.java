package org.huel.cloudhub.file.fs.meta;

import org.huel.cloudhub.file.fs.container.ContainerType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContainerMeta extends ContainerLocator, MetadataCacheable<ContainerMeta> {
    @Override
    String getLocator();

    int getBlockSize();

    int getUsedBlock();

    int getBlockCapacity();

    String getChecksum();

    @Override
    long getSerial();

    @Override
    long getVersion();

    @Override
    String getSource();

    ContainerType getContainerType();

    List<? extends BlockFileMeta> getBlockFileMetas();

    @Override
    default String getKey() {
        return getLocator();
    }

    @Override
    default ContainerMeta getMeta() {
        return this;
    }

    @Override
    default Class<ContainerMeta> getCacheableClass() {
        return ContainerMeta.class;
    }

    void writeTo(OutputStream outputStream) throws IOException;
}
