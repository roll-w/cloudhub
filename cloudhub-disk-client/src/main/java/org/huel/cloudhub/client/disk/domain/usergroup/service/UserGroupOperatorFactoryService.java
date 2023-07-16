package org.huel.cloudhub.client.disk.domain.usergroup.service;

import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.systembased.validate.ValidatorProvider;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupMember;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupErrorCode;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupException;
import org.huel.cloudhub.client.disk.domain.usergroup.repository.UserGroupMemberRepository;
import org.huel.cloudhub.client.disk.domain.usergroup.repository.UserGroupRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class UserGroupOperatorFactoryService
        implements SystemResourceOperatorFactory, UserGroupOperatorDelegate {
    private final Validator validator;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;

    public UserGroupOperatorFactoryService(ValidatorProvider validatorProvider,
                                           UserGroupRepository userGroupRepository,
                                           UserGroupMemberRepository userGroupMemberRepository) {
        validator = validatorProvider.getValidator(SystemResourceKind.USER_GROUP);
        this.userGroupRepository = userGroupRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER_GROUP;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return UserGroupOperatorDelegate.class.isAssignableFrom(clazz);
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         boolean checkDelete) {
        if (!supports(systemResource.getSystemResourceKind())) {
            throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        }
        if (systemResource instanceof UserGroup userGroup) {
            return new UserGroupOperatorImpl(userGroup, this, checkDelete);
        }
        UserGroup userGroup =
                userGroupRepository.getById(systemResource.getResourceId());
        if (userGroup == null) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_NOT_FOUND);
        }
        return new UserGroupOperatorImpl(userGroup, this, checkDelete);
    }

    @Override
    public void updateUserGroup(UserGroup userGroup) {
        userGroupRepository.update(userGroup);
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public UserGroupMember getUserGroupMember(StorageOwner storageOwner) {
        return userGroupMemberRepository.getByUser(storageOwner);
    }

    @Override
    public Long createUserGroupMember(UserGroupMember userGroupMember) {
        return userGroupMemberRepository.insert(userGroupMember);
    }

    @Override
    public void updateUserGroupMember(UserGroupMember userGroupMember) {
        userGroupMemberRepository.update(userGroupMember);
    }
}
