package org.huel.cloudhub.client.disk.domain.share.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserShareDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserShareRepository extends BaseRepository<UserShare> {
    private final UserShareDao userShareDao;

    protected UserShareRepository(DiskDatabase database,
                                  CacheManager cacheManager) {
        super(database.getUserShareDao(), cacheManager);
        userShareDao = database.getUserShareDao();
    }

    public UserShare getByShareId(String shareId) {
        return cacheResult(userShareDao.getByShareId(shareId));
    }

    public List<UserShare> getByUserId(long userId) {
        return cacheResult(userShareDao.getByUserId(userId));
    }

    public List<UserShare> getByUserId(long userId, Offset offset) {
        return cacheResult(userShareDao.getByUserId(userId, offset));
    }

    public List<UserShare> getByStorage(long storageId, StorageType storageType) {
        return cacheResult(
                userShareDao.getByStorage(storageId, storageType)
        );
    }

    public List<UserShare> getByStorage(long storageId, StorageType storageType, Offset offset) {
        return cacheResult(
                userShareDao.getByStorage(storageId, storageType, offset)
        );
    }

    @Override
    protected Class<UserShare> getEntityClass() {
        return UserShare.class;
    }
}
