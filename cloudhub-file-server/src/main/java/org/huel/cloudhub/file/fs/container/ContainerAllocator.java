package org.huel.cloudhub.file.fs.container;

import space.lingu.NonNull;

import java.util.List;

/**
 * Container 分配提供接口。
 *
 * @author RollW
 */
public interface ContainerAllocator extends ContainerProvider {
    String CONTAINER_META_SUFFIX = ".cmeta";

    @NonNull
    Container allocateNewContainer(String id);

    // TODO: replace allocateContainer with this method.
    @NonNull
    List<Container> allocateContainers(String id, long size);

    boolean dataExists(String fileId);
}
