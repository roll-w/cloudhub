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
public class UserSettingServiceImpl implements UserSettingService{

    private final UserRepository userRepository;


    public UserSettingServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    无令牌
    @Override
    public PasswordResetToken createPasswordResetToken(long userId) {
        return null;
    }
    //更新密码
    @Override
    public MessagePackage<UserInfo> resetPassword(long userId, String oldPassword, String newPassword) {
        User user = userRepository.getUserById(userId);
        if (Objects.equals(user.getPassword(), oldPassword)){
            User user_New = user.setPassword(newPassword);
            userRepository.update(user_New);
            new MessagePackage<>(ErrorCode.SUCCESS, user.toInfo());
        }
        return new MessagePackage<>(ErrorCode.ERROR_PASSWORD_NOT_CORRECT, "OldPassword Error",null);
    }
    //忘记密码
    @Override
    public MessagePackage<UserInfo> resetPassword(long userId, String newPassword) {
        User user = userRepository.getUserById(userId);

        userRepository.update(user.setPassword(newPassword));
        return new MessagePackage<>(ErrorCode.SUCCESS, user.toInfo());
    }

    @Override
    public MessagePackage<UserInfo> resetPassword(long userId, String newPassword, PasswordResetToken resetToken) {
        return null;
    }
    //更换用户名
    @Override
    public MessagePackage<UserInfo> resetUsername(long userId, String newUsername) {
        User user = userRepository.getUserById(userId);
        userRepository.update(user.setUsername(newUsername));
        return new MessagePackage<>(ErrorCode.SUCCESS, user.toInfo());
    }
}
