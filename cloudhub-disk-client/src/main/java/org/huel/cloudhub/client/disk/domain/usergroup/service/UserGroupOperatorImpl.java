package org.huel.cloudhub.client.disk.domain.usergroup.service;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.usergroup.GroupSettingKeys;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupMember;
import org.huel.cloudhub.client.disk.domain.usergroup.UserGroupOperator;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupErrorCode;
import org.huel.cloudhub.client.disk.domain.usergroup.common.UserGroupException;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.lingu.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class UserGroupOperatorImpl implements UserGroupOperator {
    private static final Logger logger = LoggerFactory.getLogger(UserGroupOperatorImpl.class);

    private UserGroup userGroup;
    private final UserGroup.Builder userGroupBuilder;
    private final UserGroupOperatorDelegate delegate;
    private boolean autoUpdate = true;
    private boolean checkDeleted;
    private boolean updateFlag = false;

    public UserGroupOperatorImpl(UserGroup userGroup,
                                 UserGroupOperatorDelegate delegate,
                                 boolean checkDeleted) {
        this.delegate = delegate;
        this.userGroup = userGroup;
        this.userGroupBuilder = userGroup.toBuilder();
        this.checkDeleted = checkDeleted;
    }

    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        this.checkDeleted = checkDeleted;
    }

    @Override
    public boolean isCheckDeleted() {
        return checkDeleted;
    }

    @Override
    public long getResourceId() {
        return userGroup.getResourceId();
    }

    @Override
    public UserGroupOperator update() throws BusinessRuntimeException {
        if (!autoUpdate && updateFlag) {
            userGroup = userGroupBuilder
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateUserGroup(userGroup);
            updateFlag = false;
            return this;
        }
        return this;
    }

    @Override
    public UserGroupOperator delete() throws BusinessRuntimeException {
        checkDeleted();
        userGroup = userGroupBuilder
                .setDeleted(true)
                .build();

        return updateInternal();
    }

    @Override
    public UserGroupOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException {
        checkDeleted();
        if (userGroup.getName().equals(newName)) {
            return this;
        }
        delegate.getValidator().validateThrows(newName, FieldType.NAME);
        userGroupBuilder.setName(newName);

        return updateInternal();
    }

    @Override
    public UserGroupOperator disableAutoUpdate() {
        autoUpdate = false;
        return updateInternal();
    }

    @Override
    public UserGroupOperator enableAutoUpdate() {
        autoUpdate = true;
        return updateInternal();
    }

    @Override
    public boolean isAutoUpdateEnabled() {
        return autoUpdate;
    }

    @Override
    public UserGroupOperator setName(String name) {
        return rename(name);
    }

    @Override
    public UserGroupOperator setDescription(String description) {
        checkDeleted();
        if (userGroup.getDescription().equals(description)) {
            return this;
        }
        delegate.getValidator().validateThrows(description, FieldType.DESCRIPTION);
        userGroupBuilder.setDescription(description);
        return updateInternal();
    }

    @Override
    public UserGroupOperator setSettings(Map<String, String> settings) {
        checkDeleted();
        if (settings == null || settings.isEmpty()) {
            userGroupBuilder.setSettings(new HashMap<>());
            return updateInternal();
        }
        if (userGroup.getSettings().equals(settings)) {
            return this;
        }
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            checkKeyValueSupports(entry.getKey(), entry.getValue());
        }
        userGroupBuilder.setSettings(settings);
        return updateInternal();
    }

    @Override
    public UserGroupOperator setSetting(String key, String value) {
        checkDeleted();
        checkKeyValueSupports(key, value);

        userGroupBuilder.setSetting(key, value);

        return updateInternal();
    }

    private void checkKeyValueSupports(String key, String value) {
        if (Strings.isNullOrEmpty(key)) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_SETTINGS_KEY_INVALID,
                    "Key is null or empty.");
        }
        if (Strings.isNullOrEmpty(value)) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_SETTINGS_VALUE_INVALID,
                    "Value is null or empty.");
        }
        String gotValue = GroupSettingKeys.DEFAULT.getSettings().get(key);
        if (gotValue == null) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_SETTINGS_KEY_INVALID,
                    "Key is not supported.");
        }
        try {
            Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_SETTINGS_VALUE_INVALID,
                    "Value is not a number.");
        }
    }

    @Override
    public UserGroupOperator addMember(@NonNull StorageOwner storageOwner) {
        checkDeleted();

        UserGroupMember member = delegate.getUserGroupMember(storageOwner);
        if (member == null) {
            long now = System.currentTimeMillis();
            UserGroupMember newMember = UserGroupMember.builder()
                    .setGroupId(userGroup.getId())
                    .setUserId(storageOwner.getOwnerId())
                    .setUserType(storageOwner.getOwnerType())
                    .setCreateTime(now)
                    .setUpdateTime(now)
                    .build();
            long memberId = delegate.createUserGroupMember(newMember);

            return this;
        }
        if (member.isDeleted()) {
            UserGroupMember updated = member.toBuilder()
                    .setDeleted(false)
                    .setGroupId(userGroup.getId())
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateUserGroupMember(updated);
            return this;
        }
        if (member.getGroupId() != userGroup.getId()) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_MEMBER_ALREADY_EXISTS,
                    "Member already exists in another group.");
        }

        return this;
    }

    @Override
    public UserGroupOperator removeMember(@NonNull StorageOwner storageOwner) {
        checkDeleted();

        UserGroupMember member = delegate.getUserGroupMember(storageOwner);
        if (member == null) {
            return this;
        }
        if (member.getGroupId() != userGroup.getId()) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_MEMBER_NOT_EXISTS,
                    "Member not exists in this group.");
        }
        if (member.isDeleted()) {
            return this;
        }
        UserGroupMember updated = member.toBuilder()
                .setDeleted(true)
                .setUpdateTime(System.currentTimeMillis())
                .build();
        delegate.updateUserGroupMember(updated);
        return this;
    }

    private UserGroupOperator updateInternal() {
        if (!autoUpdate) {
            return this;
        }
        userGroup = userGroupBuilder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        delegate.updateUserGroup(userGroup);
        return this;
    }

    private void checkDeleted() {
        if (!checkDeleted) {
            return;
        }
        if (userGroup.isDeleted()) {
            throw new UserGroupException(UserGroupErrorCode.ERROR_GROUP_DELETED);
        }
    }

    @Override
    public UserGroup getUserGroup() {
        return userGroup;
    }
}
