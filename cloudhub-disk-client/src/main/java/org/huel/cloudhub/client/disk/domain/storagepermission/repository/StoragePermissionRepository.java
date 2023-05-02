package org.huel.cloudhub.client.disk.domain.storagepermission.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.StoragePermissionDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermission;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class StoragePermissionRepository extends BaseRepository<StoragePermission> {
    private final StoragePermissionDao storagePermissionDao;

    public StoragePermissionRepository(DiskDatabase database) {
        super(database.getStoragePermissionDao());
        storagePermissionDao = database.getStoragePermissionDao();
    }

    public StoragePermission getStoragePermission(long storageId,
                                                  StorageType storageType) {
        return storagePermissionDao.getStoragePermission(storageId, storageType);
    }

    public StoragePermission getById(long id) {
        return storagePermissionDao.getById(id);
    }
}
