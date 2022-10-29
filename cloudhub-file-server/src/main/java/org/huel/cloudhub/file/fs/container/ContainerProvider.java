package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.io.SeekableInputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
public interface ContainerProvider {
    default SeekableInputStream openContainer(Container container) throws IOException {
        return openContainer(container.getLocation());
    }

    SeekableInputStream openContainer(ContainerLocation location) throws IOException;

    @Nullable
    Container findContainer(String containerId, long serial);

    @NonNull
    List<Container> findContainersByFile(String fileId);

    ContainerGroup findContainerGroupByFile(String fileId);
}
