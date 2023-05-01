package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermission;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface StoragePermissionDao extends AutoPrimaryBaseDao<StoragePermission> {
    @Query("SELECT * FROM storage_permission WHERE storage_id = {storageId} AND storage_type = {storageType}")
    List<StoragePermission> getStoragePermissions(long storageId, StorageType storageType);

    @Query("SELECT * FROM storage_permission WHERE id = {id}")
    StoragePermission getById(long id);

}
