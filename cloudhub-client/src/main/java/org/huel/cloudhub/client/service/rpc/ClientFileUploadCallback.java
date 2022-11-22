package org.huel.cloudhub.client.service.rpc;

/**
 * @author RollW
 */
public interface ClientFileUploadCallback {
    void onComplete(boolean success, String fileId, long fileSize);
}
