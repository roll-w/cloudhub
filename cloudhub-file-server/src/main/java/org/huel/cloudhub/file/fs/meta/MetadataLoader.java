package org.huel.cloudhub.file.fs.meta;

import java.io.IOException;

/**
 * Load container meta and block file meta from index file.
 * Allows memory cache control.
 *
 * @author RollW
 */
public interface MetadataLoader {
    ContainerMeta loadContainerMeta(ContainerLocator containerLocator)
            throws IOException, MetadataException;

    void setMetadataCacheStrategy(MetadataCacheStrategy strategy);
}
