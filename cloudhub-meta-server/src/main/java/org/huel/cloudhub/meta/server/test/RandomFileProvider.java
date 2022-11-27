package org.huel.cloudhub.meta.server.test;

import java.io.File;

/**
 * @author RollW
 */
public interface RandomFileProvider {
    File openTempFile();

    void releaseTempFile();
}
