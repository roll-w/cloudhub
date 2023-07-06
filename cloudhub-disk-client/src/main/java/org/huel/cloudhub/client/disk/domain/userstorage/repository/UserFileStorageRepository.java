package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserFileStorageDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserFileStorageRepository extends BaseRepository<UserFileStorage> {
    private final UserFileStorageDao fileStorageDao;

    public UserFileStorageRepository(DiskDatabase diskDatabase,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(diskDatabase.getUserFileStorageDao(), pageableContextThreadAware, cacheManager);
        this.fileStorageDao = diskDatabase.getUserFileStorageDao();
    }

    public List<UserFileStorage> get(long owner, LegalUserType legalUserType) {
        return cacheResult(
                fileStorageDao.get(owner, legalUserType)
        );
    }

    public List<UserFileStorage> getByDirectoryId(long directoryId, long owner,
                                                  LegalUserType legalUserType) {
        return cacheResult(
                fileStorageDao.getByDirectoryId(directoryId, owner, legalUserType)
        );
    }

    public List<UserFileStorage> getByDirectoryId(long directoryId) {
        return cacheResult(
                fileStorageDao.getByDirectoryId(directoryId)
        );
    }

    public List<UserFileStorage> getByType(long owner,
                                           LegalUserType legalUserType,
                                           FileType fileType) {
        return cacheResult(
                fileStorageDao.getByType(owner, legalUserType, fileType)
        );
    }

    public UserFileStorage getById(long owner, LegalUserType legalUserType,
                                   long directoryId, String name) {
        return cacheResult(
                fileStorageDao.getById(owner, legalUserType, directoryId, name)
        );
    }

    public List<UserFileStorage> getByIds(
            List<Long> storageIds,
            StorageOwner storageOwner) {
        List<UserFileStorage> storages = getByIds(storageIds);
        if (storages.isEmpty()) {
            return List.of();
        }

        storages.removeIf(storage -> storage.getOwner() != storageOwner.getOwnerId()
                || storage.getOwnerType() != storageOwner.getOwnerType());
        return storages;
    }

    public List<UserFileStorage> getByIdsAndType(List<Long> storageIds,
                                                 FileType fileType,
                                                 StorageOwner storageOwner) {
        List<UserFileStorage> storages =
                fileStorageDao.getByIds(storageIds);
        storages.removeIf(storage -> storage.getOwner() != storageOwner.getOwnerId()
                || storage.getOwnerType() != storageOwner.getOwnerType()
                || storage.getFileType() != fileType);
        return storages;
    }

    public List<UserFileStorage> getDeletedByOwner(long owner, LegalUserType legalUserType) {
        return cacheResult(
                fileStorageDao.getDeletedByOwner(owner, legalUserType)
        );
    }

    public List<UserFileStorage> getFilesLike(String name, long owner,
                                              LegalUserType legalUserType) {
        return cacheResult(
                fileStorageDao.findFilesLike(name, owner, legalUserType)
        );
    }

    @Override
    protected Class<UserFileStorage> getEntityClass() {
        return UserFileStorage.class;
    }

    public UserFileStorage getById(long fileId, long ownerId, LegalUserType ownerType) {
        UserFileStorage storage = getById(fileId);
        if (storage == null) {
            return null;
        }
        if (storage.getOwner() != ownerId || storage.getOwnerType() != ownerType) {
            return null;
        }
        return storage;
    }

    public UserFileStorage getByName(String name, long parentId) {
        return cacheResult(
                fileStorageDao.getByName(name, parentId)
        );
    }

    public List<UserFileStorage> findFilesByConditions(
            StorageOwner storageOwner,
            String name,
            FileType fileType,
            Long before,
            Long after) {
        return cacheResult(
                fileStorageDao.findFilesByConditions(storageOwner, name, fileType, before, after)
        );
    }

    public List<UserFileStorage> getActiveByOwner(StorageOwner storageOwner, Offset offset) {
        if (offset == null) {
            return cacheResult(
                    fileStorageDao.getActiveByOwner(storageOwner)
            );
        }

        return cacheResult(
                fileStorageDao.getActiveByOwner(storageOwner, offset)
        );
    }

    public List<UserFileStorage> getByOwner(StorageOwner storageOwner, Offset offset) {
        if (offset == null) {
            return cacheResult(
                    fileStorageDao.getByOwner(storageOwner)
            );
        }

        return cacheResult(
                fileStorageDao.getByOwner(storageOwner, offset)
        );
    }

    public int countActiveByOwner(StorageOwner storageOwner) {
        return fileStorageDao.countActiveByOwner(storageOwner);
    }

    public int countByOwner(StorageOwner storageOwner) {
        return fileStorageDao.countByOwner(storageOwner);
    }

}
