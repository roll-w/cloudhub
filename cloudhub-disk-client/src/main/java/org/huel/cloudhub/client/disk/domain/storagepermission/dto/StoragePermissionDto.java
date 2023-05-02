package org.huel.cloudhub.client.disk.domain.storagepermission.dto;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.disk.domain.storagepermission.PermissionType;
import org.huel.cloudhub.client.disk.domain.storagepermission.PublicPermissionType;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

import java.util.List;

/**
 * @author RollW
 */
public record StoragePermissionDto(
        long ownerId,
        LegalUserType ownerType,
        long storageId,
        StorageType storageType,
        long operator,
        List<PermissionType> permissions,

        @Nullable
        PublicPermissionType publicPermissionType
) {
    public boolean allowRead() {
        if (operator == ownerId) {
            return true;
        }

        if (denied()) {
            return false;
        }

        boolean userRead = permissions.contains(PermissionType.READ);
        if (userRead) {
            return true;
        }
        return publicPermissionType != null && publicPermissionType.isRead();
    }

    public boolean allowWrite() {
        if (operator == ownerId) {
            return true;
        }
        if (denied()) {
            return false;
        }

        boolean userWrite = permissions.contains(PermissionType.WRITE);
        if (userWrite) {
            return true;
        }
        return publicPermissionType != null && publicPermissionType.isWrite();
    }

    public boolean denied() {
        if (operator == ownerId) {
            return false;
        }

        return permissions.contains(PermissionType.DENIED);
    }

    public static StoragePermissionDto of(Storage storage, long operator,
                                          List<PermissionType> permissions) {
        return new StoragePermissionDto(
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getStorageId(),
                storage.getStorageType(),
                operator,
                permissions,
                null
        );
    }

    public static StoragePermissionDto of(Storage storage, long operator,
                                          List<PermissionType> permissions,
                                          PublicPermissionType publicPermissionType) {
        return new StoragePermissionDto(
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getStorageId(),
                storage.getStorageType(),
                operator,
                permissions,
                publicPermissionType
        );
    }
}
