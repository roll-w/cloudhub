package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.meta.MetaException;
import space.lingu.NonNull;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ContainerAllocator extends ContainerCreator {
    String CONTAINER_META_FILE_SUFFIX = ".cmeta";

    @NonNull
    Container allocateContainer(String id);

    boolean dataExists(String fileId);

    @Override
    void createsContainerFileWithMeta(Container container) throws IOException;

    void updatesContainerMetadata(Container container) throws MetaException, IOException;
}
