package org.huel.cloudhub.client.disk.domain.storagepermission.service;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermission;
import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;

/**
 * @author RollW
 */
public interface StoragePermissionActionDelegate {
    void updateStoragePermission(StoragePermission permission);

    void updateUserStoragePermission(StorageUserPermission permission);

    StorageUserPermission getUserStoragePermission(
            Operator operator, StorageIdentity storageIdentity);

    long createUserStoragePermission(StorageUserPermission storageUserPermission);

    long createStoragePermission(StoragePermission permission);
}
