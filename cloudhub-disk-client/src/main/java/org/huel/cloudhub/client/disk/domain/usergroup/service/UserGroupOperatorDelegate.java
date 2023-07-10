package org.huel.cloudhub.client.disk.domain.usergroup.service;

import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupMember;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public interface UserGroupOperatorDelegate {
    void updateUserGroup(UserGroup userGroup);

    Validator getValidator();

    UserGroupMember getUserGroupMember(StorageOwner storageOwner);

    Long createUserGroupMember(UserGroupMember userGroupMember);

    void updateUserGroupMember(UserGroupMember userGroupMember);
}
