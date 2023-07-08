package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.common.ParameterMissingException;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.UserOperator;
import org.huel.cloudhub.client.disk.domain.user.common.UserException;
import org.huel.cloudhub.client.disk.domain.user.filter.UserFilteringInfo;
import org.huel.cloudhub.client.disk.domain.user.filter.UserFilteringInfoType;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.ErrorCode;
import org.huel.cloudhub.web.UserErrorCode;

/**
 * @author RollW
 */
public class UserOperatorImpl implements UserOperator {
    private boolean checkDelete;
    private boolean autoUpdateEnabled;

    private User user;
    private final User.Builder userBuilder;
    private final UserOperatorDelegate delegate;

    private boolean updateFlag = false;

    public UserOperatorImpl(User user, UserOperatorDelegate delegate,
                            boolean checkDelete) {
        this.user = user;
        this.userBuilder = user.toBuilder();
        this.delegate = delegate;
        this.checkDelete = checkDelete;
    }

    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        this.checkDelete = checkDeleted;
    }

    @Override
    public boolean isCheckDeleted() {
        return checkDelete;
    }

    @Override
    public long getResourceId() {
        return getUserId();
    }

    @Override
    public UserOperator disableAutoUpdate() {
        this.autoUpdateEnabled = false;
        return this;
    }

    @Override
    public UserOperator enableAutoUpdate() {
        this.autoUpdateEnabled = true;
        return this;
    }

    @Override
    public boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled;
    }

    @Override
    public UserOperator update() throws BusinessRuntimeException {
        if (!autoUpdateEnabled && updateFlag) {
            user = userBuilder
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateUser(user);
            updateFlag = false;
            return this;
        }
        return this;
    }

    @Override
    public UserOperator delete() throws BusinessRuntimeException {
        checkDelete();
        userBuilder
                .setCanceled(true)
                .build();
        return updateInternal();
    }

    @Override
    public UserOperator rename(String username) throws BusinessRuntimeException, UnsupportedOperationException {
        checkDelete();
        if (username == null) {
            throw new ParameterMissingException("username");
        }
        if (user.getUsername().equals(username)) {
            return this;
        }
        checkRule(username, UserFilteringInfoType.USERNAME);

        if (delegate.checkUsernameExist(username)) {
            throw new UserException(UserErrorCode.ERROR_USER_EXISTED);
        }

        userBuilder.setUsername(username);
        return updateInternal();
    }

    @Override
    public UserOperator setNickname(String nickname) throws BusinessRuntimeException {
        checkDelete();
        if (nickname == null) {
            throw new ParameterMissingException("nickname");
        }
        if (user.getNickname().equals(nickname)) {
            return this;
        }

        checkRule(nickname, UserFilteringInfoType.NICKNAME);
        userBuilder.setNickname(nickname);
        return updateInternal();
    }

    @Override
    public UserOperator setEmail(String email)
            throws BusinessRuntimeException {
        checkDelete();
        if (email == null) {
            throw new ParameterMissingException("email");
        }
        if (user.getEmail().equals(email)) {
            return this;
        }

        checkRule(email, UserFilteringInfoType.EMAIL);
        if (delegate.checkEmailExist(email)) {
            throw new UserException(UserErrorCode.ERROR_EMAIL_EXISTED);
        }

        userBuilder.setEmail(email);
        return updateInternal();
    }

    @Override
    public UserOperator setRole(Role role) throws BusinessRuntimeException {
        checkDelete();
        if (role == null) {
            throw new ParameterMissingException("role");
        }
        if (user.getRole().equals(role)) {
            return this;
        }

        userBuilder.setRole(role);
        return updateInternal();
    }

    @Override
    public UserOperator setPassword(String password) throws BusinessRuntimeException {
        checkDelete();
        if (password == null) {
            throw new ParameterMissingException("password");
        }
        if (user.getPassword().equals(password)) {
            return this;
        }

        checkRule(password, UserFilteringInfoType.PASSWORD);
        userBuilder.setPassword(password);
        return updateInternal();
    }

    private void checkRule(String value, UserFilteringInfoType type) {
        UserFilteringInfo info =
                new UserFilteringInfo(value, type);
        ErrorCode errorCode =
                delegate.getUserInfoFilter().filter(info);
        if (errorCode.failed()) {
            throw new UserException(errorCode);
        }
    }

    @Override
    public UserOperator setPassword(String oldPassword,
                                    String password)
            throws BusinessRuntimeException {
        checkDelete();
        if (oldPassword == null) {
            throw new ParameterMissingException("oldPassword");
        }
        if (password == null) {
            throw new ParameterMissingException("password");
        }
        if (!delegate.validatePassword(user, oldPassword)) {
            throw new UserException(UserErrorCode.ERROR_PASSWORD_NOT_CORRECT);
        }
        checkRule(password, UserFilteringInfoType.PASSWORD);
        userBuilder.setPassword(password);

        return updateInternal();
    }

    @Override
    public UserOperator setEnabled(boolean enabled) throws BusinessRuntimeException {
        checkDelete();
        if (user.isEnabled() == enabled) {
            return this;
        }
        userBuilder.setEnabled(enabled);
        return updateInternal();
    }

    @Override
    public UserOperator setLocked(boolean locked) throws BusinessRuntimeException {
        checkDelete();
        if (user.isLocked() == locked) {
            return this;
        }
        userBuilder.setLocked(locked);
        return updateInternal();
    }

    @Override
    public UserOperator setCanceled(boolean canceled) throws BusinessRuntimeException {
        checkDelete();
        if (user.isCanceled() == canceled) {
            return this;
        }
        userBuilder.setCanceled(canceled);
        return updateInternal();
    }

    @Override
    public UserOperator getSystemResource() {
        return this;
    }

    private void checkDelete() {
        if (!checkDelete) {
            return;
        }
        if (!user.isEnabled()) {
            throw new UserException(UserErrorCode.ERROR_USER_DISABLED);
        }
        if (user.isCanceled()) {
            throw new UserException(UserErrorCode.ERROR_USER_CANCELED);
        }
    }

    private UserOperator updateInternal() {
        if (autoUpdateEnabled) {
            user = userBuilder
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateUser(user);
        }
        updateFlag = true;
        return this;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public long getUserId() {
        return user.getUserId();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getNickname() {
        return user.getNickname();
    }

    @Override
    public Role getRole() {
        return user.getRole();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @Override
    public boolean isLocked() {
        return user.isLocked();
    }

    @Override
    public boolean isCanceled() {
        return user.isCanceled();
    }

    @Override
    public long getCreateTime() {
        return user.getCreateTime();
    }

    @Override
    public long getUpdateTime() {
        return user.getUpdateTime();
    }
}
