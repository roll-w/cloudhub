package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.io.SeekableInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public interface ContainerReadOpener {
    SeekableInputStream openContainerRead(Container container)
            throws IOException, LockException;

    void closeContainerRead(Container container, InputStream stream);
}
