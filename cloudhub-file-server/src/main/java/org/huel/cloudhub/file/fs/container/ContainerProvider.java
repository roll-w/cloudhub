package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.io.SeekableInputStream;

/**
 * @author RollW
 */
public interface ContainerProvider {
    default SeekableInputStream openContainer(Container container) {
        return openContainer(container.getLocation());
    }

    SeekableInputStream openContainer(ContainerLocation location);

    @Nullable
    Container findContainer(String containerId, long serial);
}
