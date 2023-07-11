package org.huel.cloudhub.client.disk.domain.storageprocess.service;

import org.huel.cloudhub.client.disk.domain.storageprocess.StorageProcessingEvent;

/**
 * For storage process service internal use.
 *
 * @author RollW
 */
public interface StorageProcessingCallback {
    void onProcessed(StorageProcessingEvent storageProcessingEvent);
}
