package org.huel.cloudhub.client.disk.controller.favorite;

import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;

/**
 * @author RollW
 */
public record FavoriteItemCreateRequest(
        StorageType storageType,
        Long storageId
) {

    public StorageIdentity toStorageIdentity() {
        return new SimpleStorageIdentity(storageId, storageType);
    }
}
