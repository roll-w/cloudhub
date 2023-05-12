package org.huel.cloudhub.client.disk.domain.storagepermission.dto;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.disk.domain.storagepermission.PublicPermissionType;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermission;
import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

import java.util.List;

/**
 * @author RollW
 */
public record StoragePermissionsInfo(
        long ownerId,
        LegalUserType ownerType,
        long storageId,
        StorageType storageType,
        List<SimpleUserPermission> userPermissions,
        @Nullable
        PublicPermissionType publicPermission
) {

    public static StoragePermissionsInfo of(AttributedStorage storage,
                                            StoragePermission storagePermission,
                                            List<StorageUserPermission> storageUserPermissions) {
        return new StoragePermissionsInfo(
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getStorageId(),
                storage.getStorageType(),
                SimpleUserPermission.of(storageUserPermissions),
                storagePermission.getPermissionType()
        );
    }
}
