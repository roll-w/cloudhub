package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.meta.MetaException;
import space.lingu.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
public interface ContainerAllocator extends ContainerCreator {
    String CONTAINER_META_SUFFIX = ".cmeta";

    @NonNull
    Container allocateNewContainer(String id);

    // TODO: replace allocateContainer with this method.
    @NonNull
    List<Container> allocateContainers(String id, long size);

    boolean dataExists(String fileId);

    @Override
    void createsContainerFileWithMeta(Container container) throws IOException;

    void updatesContainerMetadata(Container container) throws MetaException, IOException;
}
