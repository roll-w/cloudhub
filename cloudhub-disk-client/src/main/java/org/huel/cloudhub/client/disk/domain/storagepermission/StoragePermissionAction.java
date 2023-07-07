package org.huel.cloudhub.client.disk.domain.storagepermission;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.web.BusinessRuntimeException;

import java.util.List;

/**
 * @author RollW
 */
public interface StoragePermissionAction extends SystemResourceOperator, SystemResource {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    StoragePermissionAction update() throws BusinessRuntimeException;

    /**
     * Remove permission settings or reset to default.
     */
    @Override
    StoragePermissionAction delete() throws BusinessRuntimeException;

    StoragePermissionAction setUserPermission(
            Operator operator,
            List<PermissionType> permissionTypes) throws BusinessRuntimeException;

    StoragePermissionAction removeUserPermission(Operator operator)
            throws BusinessRuntimeException;

    StoragePermissionAction setPermission(PublicPermissionType publicPermissionType)
            throws BusinessRuntimeException;

    AttributedStorage getRelatedStorage();

    @Override
    default StoragePermissionAction rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    StoragePermissionAction getSystemResource();

    StoragePermission getStoragePermission();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.STORAGE_PERMISSION;
    }
}
