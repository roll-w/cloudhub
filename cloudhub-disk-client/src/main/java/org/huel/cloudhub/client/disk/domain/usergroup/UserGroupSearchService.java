package org.huel.cloudhub.client.disk.domain.usergroup;

import org.huel.cloudhub.client.disk.domain.usergroup.dto.UserGroupInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.web.data.page.Pageable;

import java.util.List;

/**
 * @author RollW
 */
public interface UserGroupSearchService {
    UserGroupInfo findUserGroup(long userGroupId);

    UserGroupInfo findUserGroupsByUser(StorageOwner storageOwner);

    List<? extends StorageOwner> findUserGroupMembers(long userGroupId);

    List<UserGroupInfo> getUserGroups(Pageable pageable);

}
