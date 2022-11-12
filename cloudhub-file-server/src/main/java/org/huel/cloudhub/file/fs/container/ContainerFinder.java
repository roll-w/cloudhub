package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerFinder {
    boolean dataExists(String fileId, String source);

    @Nullable
    Container findContainer(String containerId, long serial, String source);

    @NonNull
    List<Container> findContainersByFile(String fileId, String source);

    ContainerGroup findContainerGroupByFile(String fileId, String source);
}
