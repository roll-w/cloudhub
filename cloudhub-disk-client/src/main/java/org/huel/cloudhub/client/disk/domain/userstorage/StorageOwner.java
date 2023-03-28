package org.huel.cloudhub.client.disk.domain.userstorage;

/**
 * @author RollW
 */
public interface StorageOwner {
    long getOwnerId();

    OwnerType getOwnerType();
}
