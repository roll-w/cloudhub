package org.huel.cloudhub.fs;

/**
 * @author RollW
 */
public interface ClientFileUploadCallback {
    void onComplete(boolean success, String fileId, long fileSize);
}
