package org.huel.cloudhub.client.service.rpc;

/**
 * @author RollW
 */
@FunctionalInterface
public interface ClientFileDownloadCallback {
    void onComplete(boolean success);
}
