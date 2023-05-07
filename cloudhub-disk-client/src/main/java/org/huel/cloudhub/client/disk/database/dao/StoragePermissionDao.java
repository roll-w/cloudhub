package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermission;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface StoragePermissionDao extends AutoPrimaryBaseDao<StoragePermission> {
    @Query("SELECT * FROM storage_permission WHERE storage_id = {storageId} AND storage_type = {storageType}")
    StoragePermission getStoragePermission(long storageId, StorageType storageType);

    @Override
    @Query("SELECT * FROM storage_permission WHERE deleted = 0")
    List<StoragePermission> getActives();

    @Override
    @Query("SELECT * FROM storage_permission WHERE deleted = 1")
    List<StoragePermission> getInactives();

    @Override
    @Query("SELECT * FROM storage_permission WHERE id = {id}")
    StoragePermission getById(long id);

    @Override
    @Query("SELECT * FROM storage_permission WHERE id IN ({ids})")
    List<StoragePermission> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM storage_permission WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM storage_permission WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM storage_permission")
    List<StoragePermission> get();

    @Override
    @Query("SELECT COUNT(*) FROM storage_permission")
    int count();

    @Override
    @Query("SELECT * FROM storage_permission LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<StoragePermission> get(Offset offset);

    @Override
    default String getTableName() {
        return "storage_permission";
    }

}
