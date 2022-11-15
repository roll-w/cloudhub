package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.LockException;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ContainerChecker {
    // TODO:
    boolean checkContainer(Container container);

    String calculateChecksum(Container container) throws LockException, IOException;
}
