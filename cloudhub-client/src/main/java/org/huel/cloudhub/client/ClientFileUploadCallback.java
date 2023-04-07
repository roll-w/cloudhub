package org.huel.cloudhub.client;

/**
 * @author RollW
 */
public interface ClientFileUploadCallback {
    void onComplete(boolean success, String fileId, long fileSize);
}
