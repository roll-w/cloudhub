package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.user.common.UserViewException;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.huel.cloudhub.client.disk.domain.user.event.OnUserCreateEvent;
import org.huel.cloudhub.client.disk.domain.user.filter.UserFilteringInfo;
import org.huel.cloudhub.client.disk.domain.user.filter.UserFilteringInfoType;
import org.huel.cloudhub.client.disk.domain.user.filter.UserInfoFilter;
import org.huel.cloudhub.client.disk.domain.user.repository.UserRepository;
import org.huel.cloudhub.web.CommonErrorCode;
import org.huel.cloudhub.web.ErrorCode;
import org.huel.cloudhub.web.Result;
import org.huel.cloudhub.web.UserErrorCode;
import org.huel.cloudhub.web.data.page.PageHelper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserServiceImpl implements  UserSignatureProvider,
        UserManageService, UserSearchService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoFilter userInfoFilter;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserInfoFilter userInfoFilter,
                           ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInfoFilter = userInfoFilter;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String getSignature(long userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            return null;
        }
        return "LampBlogUserSignature-" + user.getPassword();
    }

    @Override
    public Result<UserInfo> createUser(String username, String password,
                                       String email, Role role, boolean enable) {
        if (userRepository.isExistByEmail(username)) {
            return Result.of(UserErrorCode.ERROR_USER_EXISTED);
        }
        if (userRepository.isExistByEmail(email)) {
            return Result.of(UserErrorCode.ERROR_EMAIL_EXISTED);
        }
        ErrorCode validateUser = validate(username, password, email);
        if (validateUser.failed()) {
            return Result.of(validateUser);
        }
        User user = User.builder()
                .setUsername(username)
                .setPassword(passwordEncoder.encode(password))
                .setRole(role)
                .setEnabled(enable)
                .setAccountExpired(false)
                .setRegisterTime(System.currentTimeMillis())
                .setEmail(email)
                .build();
        long id = userRepository.insertUser(user);
        UserInfo userInfo = new UserInfo(id, username, email, role);
        OnUserCreateEvent onUserCreateEvent =
                new OnUserCreateEvent(userInfo);
        eventPublisher.publishEvent(onUserCreateEvent);
        return Result.success(userInfo);
    }

    private ErrorCode validate(String username,
                               String password,
                               String email) {
        List<UserFilteringInfo> filteringInfos = List.of(
                new UserFilteringInfo(username, UserFilteringInfoType.USERNAME),
                new UserFilteringInfo(password, UserFilteringInfoType.PASSWORD),
                new UserFilteringInfo(email, UserFilteringInfoType.EMAIL)
        );
        for (UserFilteringInfo filteringInfo : filteringInfos) {
            ErrorCode errorCode = userInfoFilter.filter(filteringInfo);
            if (errorCode.failed()) {
                return errorCode;
            }
        }
        return CommonErrorCode.SUCCESS;
    }

    @Override
    public User getUser(long userId) throws UserViewException {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new UserViewException(UserErrorCode.ERROR_USER_NOT_EXIST);
        }
        return user;
    }

    @Override
    public List<User> getUsers(int page, int size) {
        return userRepository.getUsers(PageHelper.offset(page, size));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getAll();
    }

    @Override
    public UserIdentity findUser(long userId) throws UserViewException {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new UserViewException(UserErrorCode.ERROR_USER_NOT_EXIST);
        }
        if (!user.isEnabled()) {
            throw new UserViewException(UserErrorCode.ERROR_USER_DISABLED);
        }
        if (user.isCanceled()) {
            throw new UserViewException(UserErrorCode.ERROR_USER_CANCELED);
        }
        return user;
    }

    @Override
    public UserIdentity findUser(UserIdentity userIdentity) throws UserViewException {
        if (userIdentity == null) {
            throw new UserViewException(UserErrorCode.ERROR_USER_NOT_EXIST);
        }
        return findUser(userIdentity.getUserId());
    }

    @Override
    public List<? extends UserIdentity> findUsers(int page, int size) {
        // TODO: filter canceled user
        return userRepository.getUsers(PageHelper.offset(page, size));
    }

    @Override
    public List<? extends UserIdentity> findUsers() {
        // TODO: filter canceled user
        return userRepository.getAll();
    }

    @Override
    public List<? extends UserIdentity> findUsers(List<Long> ids) {
        return userRepository.getUsersByIds(ids);
    }

    @Override
    public void deleteUser(long userId) {

    }

    @Override
    public void setUserEnable(long userId, boolean enable) {

    }

    @Override
    public void setBlockUser(long userId, boolean block) {

    }
}
