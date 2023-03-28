package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.userstorage.OwnerType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public record StorageOwnerInfo(
        long ownerId,
        OwnerType ownerType
) implements StorageOwner {
    @Override
    public long getOwnerId() {
        return ownerId;
    }

    @Override
    public OwnerType getOwnerType() {
        return ownerType;
    }
}
