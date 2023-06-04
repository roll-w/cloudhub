package org.huel.cloudhub.client.disk.domain.usergroup.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserGroupDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class UserGroupRepository extends BaseRepository<UserGroup> {
    private final UserGroupDao userGroupDao;

    protected UserGroupRepository(DiskDatabase database,
                                  CacheManager cacheManager) {
        super(database.getUserGroupDao(), cacheManager);
        this.userGroupDao = database.getUserGroupDao();
    }

    @Override
    protected Class<UserGroup> getEntityClass() {
        return UserGroup.class;
    }

    public UserGroup getByName(String name) {
        return cacheResult(userGroupDao.getByName(name));
    }
}
