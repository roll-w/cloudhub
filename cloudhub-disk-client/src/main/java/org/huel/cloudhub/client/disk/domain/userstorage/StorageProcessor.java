package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;

/**
 * @author RollW
 */
public interface StorageProcessor {
    void onStorageCreated(Storage storage, StorageAttr storageAttr);
}
