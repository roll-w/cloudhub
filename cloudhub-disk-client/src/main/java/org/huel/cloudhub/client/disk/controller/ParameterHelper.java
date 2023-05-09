package org.huel.cloudhub.client.disk.controller;

import org.huel.cloudhub.client.disk.common.ParameterFailedException;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageOwner;

/**
 * @author RollW
 */
public final class ParameterHelper {

    public static StorageOwner buildStorageOwner(long ownerId, String ownerType) {
        LegalUserType legalUserType = LegalUserType.from(ownerType);
        if (legalUserType == null) {
            throw new ParameterFailedException("{}", "ownerType is not valid");
        }
        return new SimpleStorageOwner(ownerId, legalUserType);
    }

    public static StorageIdentity buildStorageIdentity(long storageId, String storageType) {
        StorageType type = StorageType.from(storageType);
        if (type == null) {
            throw new ParameterFailedException("{}", "storageType is not valid");
        }
        return new SimpleStorageIdentity(storageId, type);
    }

    private ParameterHelper() {
    }
}
