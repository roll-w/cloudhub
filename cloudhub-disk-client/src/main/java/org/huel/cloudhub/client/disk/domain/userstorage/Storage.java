package org.huel.cloudhub.client.disk.domain.userstorage;

/**
 * @author RollW
 */
public interface Storage extends StorageOwner {
    long getStorageId();

    StorageType getStorageType();

    Long getParentId();

    long getOwnerId();

    OwnerType getOwnerType();
}
