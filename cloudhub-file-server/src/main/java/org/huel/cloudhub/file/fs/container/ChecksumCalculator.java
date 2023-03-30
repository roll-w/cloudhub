package org.huel.cloudhub.file.fs.container;

import org.huel.cloudhub.file.fs.ServerFile;

import java.io.IOException;

/**
 * @author RollW
 */
public interface ChecksumCalculator {
    String calculateChecksum(ServerFile file) throws IOException;
}
