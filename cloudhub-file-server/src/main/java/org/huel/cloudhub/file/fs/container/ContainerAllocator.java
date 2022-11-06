package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.Nullable;
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
    String LOCAL = "[LOCAL]";

    @NonNull
    Container allocateNewContainer(String id, String source);

    @NonNull
    List<Container> allocateContainers(String id, long size, String source);

    boolean dataExists(String fileId, String source);

    @Nullable
    Container findContainer(String containerId, long serial, String source);

    @NonNull
    List<Container> findContainersByFile(String fileId, String source);

    ContainerGroup findContainerGroupByFile(String fileId, String source);

    void createsContainerFileWithMeta(Container container) throws IOException;

    void updatesContainerMetadata(Container container) throws MetaException, IOException;
}
