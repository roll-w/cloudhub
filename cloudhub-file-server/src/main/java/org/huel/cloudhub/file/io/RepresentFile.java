package org.huel.cloudhub.file.io;

import java.io.File;

/**
 *
 * @author RollW
 */
public interface RepresentFile {
    String getLocalPath();

    File toFile();
}
