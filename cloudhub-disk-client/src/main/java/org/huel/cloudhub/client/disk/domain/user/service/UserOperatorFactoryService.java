package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.UserOperator;
import org.huel.cloudhub.client.disk.domain.user.common.UserException;
import org.huel.cloudhub.client.disk.domain.user.filter.UserInfoFilter;
import org.huel.cloudhub.client.disk.domain.user.repository.UserRepository;
import org.huel.cloudhub.web.UserErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class UserOperatorFactoryService implements SystemResourceOperatorFactory, UserOperatorDelegate {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoFilter userInfoFilter;

    public UserOperatorFactoryService(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      UserInfoFilter userInfoFilter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInfoFilter = userInfoFilter;
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return UserOperator.class.isAssignableFrom(clazz);
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         boolean checkDelete) {
        User user = tryGetUser(systemResource);

        return new UserOperatorImpl(user, this, checkDelete);
    }

    private User tryGetUser(SystemResource systemResource) {
        if (systemResource.getSystemResourceKind() != SystemResourceKind.USER) {
            throw new UnsupportedKindException(systemResource.getSystemResourceKind());
        }
        if (systemResource instanceof User user) {
            return user;
        }
        User user = userRepository.getById(systemResource.getResourceId());
        if (user == null) {
            throw new UserException(UserErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exist, id: " + systemResource.getResourceId()
            );
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        userRepository.update(user);
    }

    @Override
    public boolean checkUsernameExist(String username) {
        return false;
    }

    @Override
    public boolean checkEmailExist(String email) {
        return false;
    }

    @Override
    public boolean validatePassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public UserInfoFilter getUserInfoFilter() {
        return userInfoFilter;
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
