package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface Storage extends StorageIdentity, StorageOwner {
    @Override
    long getStorageId();

    @Override
    @NonNull
    StorageType getStorageType();

    String getName();

    Long getParentId();

    @Override
    long getOwnerId();

    @NonNull
    LegalUserType getOwnerType();
}
