package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.LockException;
import org.huel.cloudhub.file.io.SeekableOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
public interface ContainerWriterOpener {
    SeekableOutputStream openContainerWrite(Container container) throws IOException, LockException;

    void closeContainerWrite(Container container, OutputStream stream);
}
