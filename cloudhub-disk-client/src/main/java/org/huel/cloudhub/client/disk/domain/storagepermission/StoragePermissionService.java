package org.huel.cloudhub.client.disk.domain.storagepermission;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.storagepermission.common.StoragePermissionException;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionDto;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;

import java.util.List;

/**
 * @author RollW
 */
public interface StoragePermissionService {
    void setStoragePermission(Storage storage,
                              PublicPermissionType permissionType);

    void setStoragePermission(Storage storage, Operator operator,
                              List<PermissionType> permissionTypes);

    boolean checkPermissionOf(Storage storage,
                              Operator operator,
                              Action action);

    void checkPermissionOfThrows(Storage storage,
                                 Operator operator,
                                 Action action) throws StoragePermissionException;

    StoragePermissionDto getPermissionOf(Storage storage, Operator operator);

}