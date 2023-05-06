package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserFileStorageDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserFileStorageRepository extends BaseRepository<UserFileStorage> {
    private final UserFileStorageDao fileStorageDao;

    public UserFileStorageRepository(DiskDatabase diskDatabase) {
        super(diskDatabase.getUserFileStorageDao());
        this.fileStorageDao = diskDatabase.getUserFileStorageDao();
    }

    public List<UserFileStorage> get(long owner, LegalUserType legalUserType) {
        return fileStorageDao.get(owner, legalUserType);
    }

    public List<UserFileStorage> getByDirectoryId(long directoryId, long owner,
                                                  LegalUserType legalUserType) {
        return fileStorageDao.getByDirectoryId(directoryId, owner, legalUserType);
    }

    public List<UserFileStorage> getByDirectoryId(long directoryId) {
        return fileStorageDao.getByDirectoryId(directoryId);
    }

    public List<UserFileStorage> getByType(long owner,
                                           LegalUserType legalUserType,
                                           FileType fileType) {
        return fileStorageDao.getByType(owner, legalUserType, fileType);
    }

    public UserFileStorage getById(long owner, LegalUserType legalUserType,
                                   long directoryId, String name) {
        return fileStorageDao.getById(owner, legalUserType, directoryId, name);
    }

    public List<UserFileStorage> getByIdsAndType(List<Long> storageIds,
                                                 FileType fileType) {
        return fileStorageDao.getByIdsAndType(storageIds, fileType);
    }

    public List<UserFileStorage> getByIds(
            List<Long> storageIds,
            StorageOwner storageOwner) {
       return fileStorageDao.getByIds(storageIds, storageOwner);
    }

    public List<UserFileStorage> getByIdsAndType(List<Long> storageIds,
                                                 FileType fileType,
                                                 StorageOwner storageOwner) {
        return fileStorageDao.getByIdsAndType(storageIds, fileType, storageOwner);
    }

    public List<UserFileStorage> getDeletedByOwner(long owner, LegalUserType legalUserType) {
        return fileStorageDao.getDeletedByOwner(owner, legalUserType);
    }

    public List<UserFileStorage> getFilesLike(String name, long owner, LegalUserType legalUserType) {
        return fileStorageDao.getFilesLike(name, owner, legalUserType);
    }

    @Override
    protected Class<UserFileStorage> getEntityClass() {
        return UserFileStorage.class;
    }

    public UserFileStorage getById(long fileId, long ownerId, LegalUserType ownerType) {
        return fileStorageDao.getById(fileId, ownerId, ownerType);
    }
}
