package org.huel.cloudhub.client.disk.domain.storagepermission;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.storagepermission.common.StoragePermissionException;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionDto;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;

import java.util.List;

/**
 * @author RollW
 */
public interface StoragePermissionService {
    void setStoragePermission(StorageIdentity storage,
                              PublicPermissionType permissionType);

    void setStoragePermission(StorageIdentity storage, Operator operator,
                              List<PermissionType> permissionTypes);

    boolean checkPermissionOf(StorageIdentity storage,
                              Operator operator,
                              Action action);

    void checkPermissionOrThrows(StorageIdentity storage,
                                 Operator operator,
                                 Action action) throws StoragePermissionException;

    StoragePermissionDto getPermissionOf(StorageIdentity storage, Operator operator);

}
