package org.huel.cloudhub.client.disk.domain.storagepermission.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.StorageUserPermissionDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class StorageUserPermissionRepository extends BaseRepository<StorageUserPermission> {
    private final StorageUserPermissionDao storageUserPermissionDao;

    public StorageUserPermissionRepository(DiskDatabase database,
                                           CacheManager cacheManager) {
        super(database.getStorageUserPermissionDao(), cacheManager);
        this.storageUserPermissionDao = database.getStorageUserPermissionDao();
    }

    public List<StorageUserPermission> getStorageUserPermissions(long storageId, StorageType storageType) {
        return storageUserPermissionDao.getStorageUserPermissions(storageId, storageType);
    }


    public StorageUserPermission getByStorageIdAndUserId(long storageId, StorageType storageType,
                                                         long userId) {
        return storageUserPermissionDao.getByStorageIdAndUserId(storageId, storageType, userId);
    }
}
