package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface Storage extends StorageOwner, SystemResource {
    long getStorageId();

    String getName();

    @NonNull
    StorageType getStorageType();

    Long getParentId();

    long getOwnerId();

    @NonNull
    LegalUserType getOwnerType();

    @Override
    default long getResourceId() {
        return getStorageId();
    }

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return getStorageType().getSystemResourceKind();
    }

    default boolean isFile() {
        return getStorageType() == StorageType.FILE;
    }
}
