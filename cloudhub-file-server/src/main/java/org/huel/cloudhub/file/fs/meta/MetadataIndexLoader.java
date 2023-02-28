package org.huel.cloudhub.file.fs.meta;

import java.util.List;

/**
 * Load container meta and block file meta from index file.
 * Allows memory cache control.
 *
 * @author RollW
 */
public interface MetadataIndexLoader {
    ContainerMeta loadContainerMeta(ContainerLocator containerLocator);

    List<BlockFileMeta> loadBlockFileMeta(ContainerMeta containerMeta);
}
