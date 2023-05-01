package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface StorageUserPermissionDao extends AutoPrimaryBaseDao<StorageUserPermission> {

    @Query("SELECT * FROM storage_user_permission WHERE storage_id = {storageId} AND storage_type = {storageType}")
    List<StorageUserPermission> getStorageUserPermissions(long storageId, StorageType storageType);

    @Query("SELECT * FROM storage_user_permission WHERE id = {id}")
    StorageUserPermission getById(long id);

    @Query("SELECT * FROM storage_user_permission WHERE storage_id = {storageId} AND storage_type = {storageType} AND user_id = {userId}")
    List<StorageUserPermission> getByStorageIdAndUserId(long storageId, StorageType storageType, long userId);
}
