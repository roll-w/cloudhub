package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.web.data.page.Offset;
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

    @Query("SELECT * FROM storage_user_permission WHERE storage_id = {storageId} AND storage_type = {storageType} AND user_id = {userId}")
    StorageUserPermission getByStorageIdAndUserId(long storageId, StorageType storageType, long userId);

    @Override
    @Query("SELECT * FROM storage_user_permission WHERE deleted = 0")
    List<StorageUserPermission> getActives();

    @Override
    @Query("SELECT * FROM storage_user_permission WHERE deleted = 1")
    List<StorageUserPermission> getInactives();

    @Override
    @Query("SELECT * FROM storage_user_permission WHERE id = {id}")
    StorageUserPermission getById(long id);

    @Override
    @Query("SELECT * FROM storage_user_permission WHERE id IN {ids}")
    List<StorageUserPermission> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM storage_user_permission WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM storage_user_permission WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM storage_user_permission")
    List<StorageUserPermission> get();

    @Override
    @Query("SELECT COUNT(*) FROM storage_user_permission")
    int count();

    @Override
    @Query("SELECT * FROM storage_user_permission LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<StorageUserPermission> get(Offset offset);

    @Override
    default String getTableName() {
        return "storage_user_permission";
    }
}
