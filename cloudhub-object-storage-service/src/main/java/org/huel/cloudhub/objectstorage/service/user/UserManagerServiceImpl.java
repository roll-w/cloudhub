package org.huel.cloudhub.objectstorage.service.user;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.objectstorage.data.database.repository.UserRepository;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.huel.cloudhub.objectstorage.data.entity.user.Role;
import org.huel.cloudhub.objectstorage.data.entity.user.User;
import org.huel.cloudhub.web.MessagePackage;
import org.huel.cloudhub.web.UserErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserManagerServiceImpl implements UserManageService {

    private final UserRepository userRepository;

    public UserManagerServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User queryUser(String username) {
        return userRepository.getUserByName(username);
    }

    @Override
    public MessagePackage<UserInfo> createUser(String username, String password,
                                               String email, Role role,
                                               boolean discardEmail) {
        Validate.notEmpty(username, "Username cannot be null or empty.");
        Validate.notEmpty(password, "Password cannot be null or empty.");
        Validate.isTrue(!(!discardEmail && email == null), "Email cannot be null");

        // TODO: 还要检查用户名和密码是否合法
        if (userRepository.isExistByName(username)) {
            return new MessagePackage<>(UserErrorCode.ERROR_USER_EXISTED,
                    "User existed.", null);
        }
        Role newRole = role == null ? Role.USER : role;
        User user = new User(username, password, newRole,
                System.currentTimeMillis(), email);
        user.setEnabled(true);
        if (discardEmail) {
            userRepository.save(user);
            return new MessagePackage<>(UserErrorCode.SUCCESS, user.toInfo());
        }
        if (userRepository.isExistByEmail(email)) {
            return new MessagePackage<>(UserErrorCode.ERROR_EMAIL_EXISTED,
                    "Email address existed.", null);
        }
        return new MessagePackage<>(UserErrorCode.SUCCESS, user.toInfo());
    }

    @Override
    public MessagePackage<Void> deleteUser(long userId) {
        if (userRepository.isExistById(userId)) {
            userRepository.deleteById(userId);
            return new MessagePackage<>(UserErrorCode.SUCCESS, null);
        }
        return new MessagePackage<>(UserErrorCode.ERROR_USER_NOT_EXIST,
                "User not exist.", null);
    }

    @Override
    public MessagePackage<Void> deleteUsers(List<Long> userIds) {
        // 不管是否存在
        userRepository.deleteByIds(userIds);
        return new MessagePackage<>(UserErrorCode.SUCCESS, null);
    }

    @Override
    public MessagePackage<UserInfo> setRoleTo(long userId, Role role) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            return new MessagePackage<>(UserErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exists", null);
        }
        user.setRole(role);
        userRepository.save(user);
        return new MessagePackage<>(UserErrorCode.SUCCESS, user.toInfo());
    }

    @Override
    public List<UserInfo> getUsers() {
        return userRepository.getUserInfos();
    }
}
