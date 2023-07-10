package org.huel.cloudhub.client.disk.domain.usergroup.service;

import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceProvider;
import org.huel.cloudhub.client.disk.domain.systembased.UnsupportedKindException;
import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.systembased.validate.ValidatorProvider;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupMember;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupSearchService;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupService;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupErrorCode;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupException;
import org.huel.cloudhub.client.disk.domain.usergroup.dto.UserGroupInfo;
import org.huel.cloudhub.client.disk.domain.usergroup.repository.UserGroupMemberRepository;
import org.huel.cloudhub.client.disk.domain.usergroup.repository.UserGroupRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageOwner;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class UserGroupServiceImpl implements UserGroupService,
        UserGroupSearchService, SystemResourceProvider {
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMemberRepository userGroupMemberRepository;
    private final Validator groupValidator;

    public UserGroupServiceImpl(UserGroupRepository userGroupRepository,
                                UserGroupMemberRepository userGroupMemberRepository,
                                ValidatorProvider validatorProvider) {
        this.userGroupRepository = userGroupRepository;
        this.userGroupMemberRepository = userGroupMemberRepository;
        this.groupValidator = validatorProvider.getValidator(SystemResourceKind.USER_GROUP);
    }

    @Override
    public void createUserGroup(String name, String description) {
        groupValidator.validateThrows(name, FieldType.NAME);
        groupValidator.validateThrows(description, FieldType.DESCRIPTION);

        UserGroup userGroup = userGroupRepository.getByName(name);
        if (userGroup != null && !userGroup.isDeleted()) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_NAME_EXIST);
        }
        long now = System.currentTimeMillis();
        if (userGroup != null) {
            UserGroup updated = userGroup.toBuilder()
                    .setDescription(description)
                    .setUpdateTime(now)
                    .setSettings(Map.of())
                    .build();
            userGroupRepository.update(updated);
            OperationContextHolder.getContext()
                    .setChangedContent(updated.getName())
                    .addSystemResource(updated);
            return;
        }
        UserGroup newUserGroup = UserGroup.builder()
                .setName(name)
                .setDescription(description)
                .setDeleted(false)
                .setSettings(Map.of())
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        long userGroupId = userGroupRepository.insert(newUserGroup);
        UserGroup inserted = newUserGroup.toBuilder()
                .setId(userGroupId)
                .build();
        OperationContextHolder.getContext()
                .setChangedContent(newUserGroup.getName())
                .addSystemResource(inserted);
    }

    @Override
    public UserGroupInfo findUserGroup(long userGroupId) {
        if (userGroupId == 0) {
            return UserGroupInfo.DEFAULT;
        }

        UserGroup userGroup = userGroupRepository.getById(userGroupId);
        if (userGroup == null) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_NOT_FOUND);
        }
        return UserGroupInfo.from(userGroup);
    }

    @Override
    public UserGroupInfo findUserGroupsByUser(StorageOwner storageOwner) {
        UserGroupMember userGroupMember =
                userGroupMemberRepository.getByUser(storageOwner);
        if (userGroupMember == null) {
            return findUserGroup(0);
        }
        long groupId = userGroupMember.getGroupId();
        return findUserGroup(groupId);
    }

    @Override
    public List<? extends StorageOwner> findUserGroupMembers(long userGroupId) {
        if (userGroupId == 0) {
            return List.of();
        }
        List<UserGroupMember> userGroupMembers =
                userGroupMemberRepository.getByGroup(userGroupId);
        if (userGroupMembers.isEmpty()) {
            return List.of();
        }

        return userGroupMembers.stream()
                .map(userGroupMember -> new SimpleStorageOwner(
                        userGroupMember.getUserId(),
                        userGroupMember.getUserType()
                ))
                .toList();
    }

    @Override
    public List<UserGroupInfo> getUserGroups(Pageable pageable) {
        List<UserGroup> userGroups =
                userGroupRepository.get(pageable.toOffset());

        return userGroups.stream()
                .map(UserGroupInfo::from)
                .toList();
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER_GROUP;
    }

    @Override
    public UserGroupInfo provide(long resourceId, SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException {
        if (systemResourceKind != SystemResourceKind.USER_GROUP) {
            throw new UnsupportedKindException(systemResourceKind);
        }
        return findUserGroup(resourceId);
    }
}
