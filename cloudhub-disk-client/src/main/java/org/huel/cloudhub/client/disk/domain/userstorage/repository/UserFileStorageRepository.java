package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserFileStorageDao;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.OwnerType;
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

    public void insert(UserFileStorage userFileStorage) {
        fileStorageDao.insert(userFileStorage);
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

    public List<UserFileStorage> get(long owner, OwnerType ownerType) {
        return fileStorageDao.get(owner, ownerType);
    }

    public List<UserFileStorage> getByDirectoryId(long directoryId, long owner,
                                                  OwnerType ownerType) {
        return fileStorageDao.getByDirectoryId(directoryId, owner, ownerType);
    }

    public List<UserFileStorage> getByDirectoryId(long directoryId) {
        return fileStorageDao.getByDirectoryId(directoryId);
    }

    public List<UserFileStorage> getByType(long owner, OwnerType ownerType,
                                           FileType fileType) {
        return fileStorageDao.getByType(owner, ownerType, fileType);
    }

    public UserFileStorage getById(long id) {
        return fileStorageDao.getById(id);
    }

    public UserFileStorage getById(long owner, OwnerType ownerType,
                                   long directoryId, String name) {
        return fileStorageDao.getById(owner, ownerType, directoryId, name);
    }
}
