package org.huel.cloudhub.web.service.user;

import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.web.data.dto.UserInfo;

/**
 * @author RollW
 */
public interface UserVerifyService {
    void createVerificationToken(UserInfo user, String token);

    MessagePackage<UserInfo> verifyToken(String token);

    MessagePackage<Void> resendToken(long userId);
}
