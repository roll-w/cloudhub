package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserFolderDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserFolderRepository extends BaseRepository<UserFolder> {
    private final UserFolderDao userFolderDao;

    public UserFolderRepository(DiskDatabase diskDatabase,
                                CacheManager cacheManager) {
        super(diskDatabase.getUserDirectoryDao(), cacheManager);
        this.userFolderDao = diskDatabase.getUserDirectoryDao();
    }

    @Override
    protected Class<UserFolder> getEntityClass() {
        return UserFolder.class;
    }

    public List<UserFolder> getByParentId(long parentId) {
        return cacheResult(
                userFolderDao.getByParentId(parentId)
        );
    }

    public List<UserFolder> getByParentId(long parentId, Offset offset) {
        return cacheResult(
                userFolderDao.getByParentId(parentId, offset)
        );
    }

    public List<UserFolder> getByParentId(long parentId, long owner,
                                          LegalUserType legalUserType) {
        return cacheResult(
                userFolderDao.getByParentId(parentId, owner, legalUserType)
        );
    }

    public UserFolder getByName(String name, long parentId,
                                long owner, LegalUserType legalUserType) {
        return cacheResult(
                userFolderDao.getByName(name, parentId, owner, legalUserType)
        );
    }

    public UserFolder getByName(String name, long parentId) {
        return cacheResult(
                userFolderDao.getByName(name, parentId)
        );
    }

    public UserFolder getById(long folderId, long ownerId, LegalUserType ownerType) {
        UserFolder userFolder = getById(folderId);
        if (userFolder == null ||
                userFolder.getOwner() != ownerId ||
                userFolder.getOwnerType() != ownerType) {
            return null;
        }
        return userFolder;
    }

    public List<UserFolder> getParents(long folderId) {
        UserFolder userFolder = getById(folderId);
        if (userFolder == null || userFolder.getParentId() <= 0) {
            return List.of();
        }
        List<Long> parentFolderIds =
                userFolderDao.getParentFolderIds(userFolder.getParentId());
        return getByIds(parentFolderIds);
    }

    public List<UserFolder> getFoldersLike(String name, StorageOwner storageOwner) {
        return cacheResult(userFolderDao.findFoldersLike(name, storageOwner));
    }

    public List<UserFolder> findFoldersByCondition(StorageOwner storageOwner,
                                                   String name,
                                                   Long before,
                                                   Long after) {
        return cacheResult(
                userFolderDao.findFoldersByCondition(storageOwner, name, before, after)
        );
    }
}
