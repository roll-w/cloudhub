package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public record SimpleStorageOwner(
        long ownerId,
        LegalUserType legalUserType
) implements StorageOwner {
    @Override
    public long getOwnerId() {
        return ownerId;
    }

    @Override
    public LegalUserType getOwnerType() {
        return legalUserType;
    }
}
