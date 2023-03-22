package org.huel.cloudhub.fs;

/**
 * @author RollW
 */
@FunctionalInterface
public interface ClientFileDownloadCallback {
    void onComplete(boolean success);
}
