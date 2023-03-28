package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserDirectoryDao;
import org.huel.cloudhub.client.disk.domain.userstorage.OwnerType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserDirectoryRepository {
    private final UserDirectoryDao userDirectoryDao;

    public UserDirectoryRepository(DiskDatabase diskDatabase) {
        this.userDirectoryDao = diskDatabase.getUserDirectoryDao();
    }

    public void insert(UserDirectory userDirectories) {
        userDirectoryDao.insert(userDirectories);
    }

    public void update(UserDirectory userDirectories) {
        userDirectoryDao.update(userDirectories);
    }

    public List<UserDirectory> get() {
        return userDirectoryDao.get();
    }

    public List<UserDirectory> get(Offset offset) {
        return userDirectoryDao.get(offset);
    }

    public UserDirectory getById(long id) {
        return userDirectoryDao.getById(id);
    }

    public List<UserDirectory> getByParentId(long parentId) {
        return userDirectoryDao.getByParentId(parentId);
    }

    public List<UserDirectory> getByParentId(long parentId, Offset offset) {
        return userDirectoryDao.getByParentId(parentId, offset);
    }

    public List<UserDirectory> getByParentId(long parentId, long owner,
                                             OwnerType ownerType) {
        return userDirectoryDao.getByParentId(parentId, owner, ownerType);
    }

    public UserDirectory getByName(String name, long parentId,
                                   long owner, OwnerType ownerType) {
        return userDirectoryDao.getByName(name, parentId, owner, ownerType);
    }
}
