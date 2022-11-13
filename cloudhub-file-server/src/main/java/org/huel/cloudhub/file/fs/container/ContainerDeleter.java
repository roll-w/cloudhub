package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.container.Container;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ContainerDeleter {
    void deleteContainer(String id, long serial, String source) throws IOException;
    void deleteContainer(Container container) throws IOException;
}
