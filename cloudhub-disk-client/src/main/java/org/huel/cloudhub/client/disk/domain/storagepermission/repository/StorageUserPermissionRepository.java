package org.huel.cloudhub.client.disk.domain.storagepermission.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.StorageUserPermissionDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.storagepermission.StorageUserPermission;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
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
                                           ContextThreadAware<PageableContext> pageableContextThreadAware,
                                           CacheManager cacheManager) {
        super(database.getStorageUserPermissionDao(), pageableContextThreadAware, cacheManager);
        this.storageUserPermissionDao = database.getStorageUserPermissionDao();
    }

    public List<StorageUserPermission> getStorageUserPermissions(long storageId, StorageType storageType) {
        return cacheResult(
                storageUserPermissionDao.getStorageUserPermissions(storageId, storageType)
        );
    }

    public StorageUserPermission getByStorageIdAndUserId(long storageId, StorageType storageType,
                                                         long userId) {
        return cacheResult(
                storageUserPermissionDao.getByStorageIdAndUserId(storageId, storageType, userId)
        );
    }

    @Override
    protected Class<StorageUserPermission> getEntityClass() {
        return StorageUserPermission.class;
    }
}
