package org.huel.cloudhub.client.service.user;

import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.client.data.dto.user.UserInfo;

/**
 * @author RollW
 */
public interface UserVerifyService {
    void createVerificationToken(UserInfo user, String token);

    MessagePackage<UserInfo> verifyToken(String token);

    MessagePackage<Void> resendToken(long userId);
}
