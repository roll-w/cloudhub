package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record SimpleStorageIdentity(
        long storageId,
        StorageType storageType
) implements StorageIdentity {
    @Override
    public long getStorageId() {
        return storageId;
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return storageType;
    }

}
