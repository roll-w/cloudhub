package org.huel.cloudhub.file.fs.meta;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerMetaBuilder {
    ContainerMetaBuilder setLocator(String locator);

    ContainerMetaBuilder setVersion(long version);

    ContainerMetaBuilder setSerial(long serial);

    ContainerMetaBuilder setSource(String source);

    ContainerMetaBuilder setBlockSize(int blockSize);

    ContainerMetaBuilder setUsedBlock(int usedBlock);

    ContainerMetaBuilder setBlockCapacity(int blockCapacity);

    ContainerMetaBuilder setChecksum(String checksum);

    ContainerMetaBuilder setBlockFileMetas(List<? extends BlockFileMeta> blockFileMetas);

    ContainerMeta build();
}
