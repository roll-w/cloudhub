package org.huel.cloudhub.web.service.user;

import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.web.data.dto.UserInfo;
import org.huel.cloudhub.web.data.entity.token.PasswordResetToken;

/**
 * @author RollW
 */
// TODO
public interface UserSettingService {
    MessagePackage<PasswordResetToken> createPasswordResetToken();

    // 旧密码正确无需重置令牌
    MessagePackage<UserInfo> resetPassword(long userId, String oldPassword, String newPassword);

    // 忘记密码，需要令牌
    MessagePackage<UserInfo> resetPassword(long userId, String newPassword,
                                           PasswordResetToken resetToken);

    MessagePackage<UserInfo> resetUsername(long userId, String newUsername);
}
