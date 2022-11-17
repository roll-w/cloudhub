package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.token.PasswordResetToken;

/**
 * User info setting.
 *
 * @author RollW
 */
// TODO
public interface UserSettingService {
    PasswordResetToken createPasswordResetToken(long userId);

    // 旧密码正确无需重置令牌
    MessagePackage<UserInfo> resetPassword(long userId, String oldPassword, String newPassword);

    MessagePackage<UserInfo> resetPassword(long userId, String newPassword);

    // 忘记密码，需要令牌
    MessagePackage<UserInfo> resetPassword(long userId, String newPassword,
                                           PasswordResetToken resetToken);

    MessagePackage<UserInfo> resetUsername(long userId, String newUsername);
}
