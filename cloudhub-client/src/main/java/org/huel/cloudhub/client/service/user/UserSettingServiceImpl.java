package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.client.data.database.repository.UserRepository;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.token.PasswordResetToken;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Cheng
 */
@Service
public class UserSettingServiceImpl implements UserSettingService {

    private final UserRepository userRepository;


    public UserSettingServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 无令牌
    @Override
    public PasswordResetToken createPasswordResetToken(long userId) {
        return null;
    }

    // 更新密码
    @Override
    public MessagePackage<UserInfo> resetPassword(long userId, String oldPassword, String newPassword) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exist.", null);
        }
        if (Objects.equals(user.getPassword(), oldPassword)) {
            User userNew = user.setPassword(newPassword);
            userRepository.update(userNew);
            new MessagePackage<>(ErrorCode.SUCCESS, user.toInfo());
        }
        return new MessagePackage<>(ErrorCode.ERROR_PASSWORD_NOT_CORRECT,
                "Old password not correct.", null);
    }

    //忘记密码
    @Override
    public MessagePackage<UserInfo> resetPassword(long userId, String newPassword) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exist.", null);
        }
        userRepository.save(user.setPassword(newPassword));
        return new MessagePackage<>(ErrorCode.SUCCESS, user.toInfo());
    }

    @Override
    public MessagePackage<UserInfo> resetPassword(long userId, String newPassword,
                                                  PasswordResetToken resetToken) {
        return resetPassword(userId, newPassword);
    }

    //更换用户名
    @Override
    public MessagePackage<UserInfo> resetUsername(long userId, String newUsername) {
        if (UserChecker.checkUsername(newUsername)) {
            return new MessagePackage<>(ErrorCode.ERROR_USERNAME_NON_COMPLIANCE,
                    "Username non compliance.", null);
        }
        if (userRepository.isExistByName(newUsername)) {
            return new MessagePackage<>(ErrorCode.ERROR_USER_EXISTED,
                    "Username existed.", null);
        }
        User user = userRepository.getUserById(userId);
        userRepository.save(user.setUsername(newUsername));
        return new MessagePackage<>(ErrorCode.SUCCESS, user.toInfo());
    }
}
