package org.huel.cloudhub.client.disk.domain.userstorage.vo;

import org.huel.cloudhub.client.disk.domain.userstorage.OwnerType;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record StorageVo(
        long storageId,
        StorageType storageType,
        long ownerId,
        OwnerType ownerType
) {

    public static StorageVo from(Storage storage) {
        return new StorageVo(
                storage.getStorageId(),
                storage.getStorageType(),
                storage.getOwnerId(),
                storage.getOwnerType()
        );
    }
}
