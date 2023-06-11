package org.huel.cloudhub.client.disk.domain.usergroup.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserGroupMemberDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupMember;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserGroupMemberRepository extends BaseRepository<UserGroupMember> {
    private final UserGroupMemberDao userGroupMemberDao;

    public UserGroupMemberRepository(DiskDatabase database,
                                     CacheManager cacheManager) {
        super(database.getUserGroupMemberDao(), cacheManager);
        this.userGroupMemberDao = database.getUserGroupMemberDao();
    }

    @Override
    protected Class<UserGroupMember> getEntityClass() {
        return UserGroupMember.class;
    }

    public UserGroupMember getByUser(StorageOwner storageOwner) {
        UserGroupMember userGroupMember = searchCacheByUser(storageOwner);
        if (userGroupMember != null) {
            return userGroupMember;
        }

        return cacheResult(userGroupMemberDao.getByUser(
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType()
        ));
    }

    public List<UserGroupMember> getByGroup(long id) {
        return cacheResult(userGroupMemberDao.getByGroup(id));
    }

    private String toKey(StorageOwner storageOwner) {
        return storageOwner.getOwnerType() + ":" + storageOwner.getOwnerId();
    }

    @Override
    protected UserGroupMember cacheResult(UserGroupMember userGroupMember) {
        String userKey = toKey(userGroupMember);
        cache.put(userKey, userGroupMember);
        return super.cacheResult(userGroupMember);
    }

    private UserGroupMember searchCacheByUser(StorageOwner storageOwner) {
        String userKey = toKey(storageOwner);
        return cache.get(userKey, UserGroupMember.class);
    }
}
