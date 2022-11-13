package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.file.fs.meta.MetaException;

import java.io.IOException;
import java.util.List;

/**
 * Container 分配提供接口。
 *
 * @author RollW
 */
public interface ContainerAllocator {
    @NonNull
    Container allocateNewContainer(String id);

    @NonNull
    List<Container> allocateContainers(String id, long size);

    void createsContainerFileWithMeta(Container container) throws IOException;

    void updatesContainerMetadata(Container container) throws MetaException, IOException;

}
