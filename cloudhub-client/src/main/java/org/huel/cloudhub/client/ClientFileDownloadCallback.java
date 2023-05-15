package org.huel.cloudhub.client;

/**
 * @author RollW
 */
@FunctionalInterface
public interface ClientFileDownloadCallback {
    void onComplete(CFSStatus success);
}
