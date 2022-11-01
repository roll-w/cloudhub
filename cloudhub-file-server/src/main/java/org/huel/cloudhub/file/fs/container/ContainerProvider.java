package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.io.SeekableInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContainerProvider {
    SeekableInputStream openContainerRead(Container container) throws IOException, LockException;

    void closeContainerRead(Container container, InputStream stream);
    @Nullable
    Container findContainer(String containerId, long serial);

    @NonNull
    List<Container> findContainersByFile(String fileId);

    ContainerGroup findContainerGroupByFile(String fileId);
}
