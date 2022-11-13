package org.huel.cloudhub.file.fs.container;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerFinder {
    String LOCAL = "[LOCAL]";

    boolean dataExists(String fileId, String source);

    @Nullable
    Container findContainer(String containerId, long serial, String source);

    @NonNull
    List<Container> findContainersByFile(String fileId, String source);

    ContainerGroup findContainerGroupByFile(String fileId, String source);

    static boolean isLocal(String source) {
        if (source == null) {
            return false;
        }
        return source.equals(LOCAL);
    }
}
