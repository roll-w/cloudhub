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

    protected UserGroupMemberRepository(DiskDatabase database,
                                        CacheManager cacheManager) {
        super(database.getUserGroupMemberDao(), cacheManager);
        this.userGroupMemberDao = database.getUserGroupMemberDao();
    }

    @Override
    protected Class<UserGroupMember> getEntityClass() {
        return UserGroupMember.class;
    }

    public UserGroupMember getByUser(StorageOwner storageOwner) {
        return cacheResult(userGroupMemberDao.getByUser(
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType()
        ));
    }

    public List<UserGroupMember> getByGroup(long id) {
        return cacheResult(userGroupMemberDao.getByGroup(id));
    }

}
