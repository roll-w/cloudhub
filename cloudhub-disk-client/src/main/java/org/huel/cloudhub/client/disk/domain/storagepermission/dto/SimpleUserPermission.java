package org.huel.cloudhub.client.disk.domain.storagepermission.dto;

import org.huel.cloudhub.client.disk.domain.storagepermission.PermissionType;
import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;

import java.util.List;

/**
 * @author RollW
 */
public record SimpleUserPermission(
        long id,
        long userId,
        List<PermissionType> permissions
) {
    public static List<SimpleUserPermission> of(List<StorageUserPermission> storageUserPermissions) {
        return storageUserPermissions.stream()
                .map(storageUserPermission -> new SimpleUserPermission(
                        storageUserPermission.getId(),
                        storageUserPermission.getUserId(),
                        storageUserPermission.getPermissionTypes()
                ))
                .toList();
    }
}
