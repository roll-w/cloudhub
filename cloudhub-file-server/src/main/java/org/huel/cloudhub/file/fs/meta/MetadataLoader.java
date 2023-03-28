package org.huel.cloudhub.file.fs.meta;

import java.io.IOException;
import java.util.List;

/**
 * Load container meta and block file meta from index file.
 * Allows memory cache control.
 *
 * @author RollW
 */
public interface MetadataLoader {
    ContainerMeta loadContainerMeta(ContainerLocator containerLocator)
            throws IOException, MetadataException;

    List<BlockFileMeta> loadBlockFileMeta(ContainerMeta containerMeta)
            throws IOException, MetadataException;
}
