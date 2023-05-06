package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface StorageIdentity extends SystemResource {
    long getStorageId();

    @NonNull
    StorageType getStorageType();

    default boolean isFile() {
        return getStorageType() == StorageType.FILE;
    }

    @Override
    default long getResourceId() {
        return getStorageId();
    }

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return getStorageType().getSystemResourceKind();
    }
}
