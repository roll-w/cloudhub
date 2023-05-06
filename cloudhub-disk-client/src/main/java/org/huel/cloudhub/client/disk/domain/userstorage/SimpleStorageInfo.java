package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record SimpleStorageInfo(
        long storageId,
        String name,
        StorageType storageType,
        Long parentId,
        long ownerId,
        LegalUserType ownerType
) implements Storage {
    @Override
    public long getStorageId() {
        return storageId();
    }

    @Override
    public String getName() {
        return name();
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return storageType();
    }

    @Override
    public Long getParentId() {
        return parentId();
    }

    @Override
    public long getOwnerId() {
        return ownerId();
    }

    @NonNull
    @Override
    public LegalUserType getOwnerType() {
        return ownerType();
    }
}
