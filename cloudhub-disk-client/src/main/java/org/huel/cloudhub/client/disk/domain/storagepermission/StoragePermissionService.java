package org.huel.cloudhub.client.disk.domain.storagepermission;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.storagepermission.common.StoragePermissionException;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionDto;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionsInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public interface StoragePermissionService {
    // TODO: refactor, remove the ignoreDelete parameter

    boolean checkPermissionOf(StorageIdentity storage,
                              Operator operator,
                              Action action, boolean ignoreDelete);

    void checkPermissionOrThrows(StorageIdentity storage,
                                 Operator operator,
                                 Action action, boolean ignoreDelete) throws StoragePermissionException;

    StoragePermissionDto getPermissionOf(StorageIdentity storage,
                                         Operator operator, boolean ignoreDelete);

    StoragePermissionsInfo getPermissionOf(StorageIdentity storageIdentity, boolean ignoreDelete);

    StoragePermissionsInfo getPermissionOf(StorageIdentity storageIdentity,
                                           StorageOwner storageOwner, boolean ignoreDelete);
}
