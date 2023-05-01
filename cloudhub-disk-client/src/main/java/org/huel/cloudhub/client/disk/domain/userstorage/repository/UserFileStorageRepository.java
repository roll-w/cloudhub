package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserFileStorageDao;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserFileStorageRepository {
    private final UserFileStorageDao fileStorageDao;

    public UserFileStorageRepository(DiskDatabase diskDatabase) {
        this.fileStorageDao = diskDatabase.getUserFileStorageDao();
    }

    public long insert(UserFileStorage userFileStorage) {
        return fileStorageDao.insertReturns(userFileStorage);
    }

    public void update(UserFileStorage userFileStorage) {
        fileStorageDao.update(userFileStorage);
    }

    public List<UserFileStorage> get() {
        return fileStorageDao.get();
    }

    public List<UserFileStorage> get(Offset offset) {
        return fileStorageDao.get(offset);
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

    public List<UserFileStorage> getByType(long owner, LegalUserType legalUserType,
                                           FileType fileType) {
        return fileStorageDao.getByType(owner, legalUserType, fileType);
    }

    public UserFileStorage getById(long id) {
        return fileStorageDao.getById(id);
    }

    public UserFileStorage getById(long owner, LegalUserType legalUserType,
                                   long directoryId, String name) {
        return fileStorageDao.getById(owner, legalUserType, directoryId, name);
    }
}
