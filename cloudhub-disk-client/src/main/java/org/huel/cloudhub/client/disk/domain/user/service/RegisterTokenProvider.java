package org.huel.cloudhub.client.disk.domain.user.service;


import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.huel.cloudhub.web.ErrorCode;

/**
 * @author RollW
 */
public interface RegisterTokenProvider {
    String createRegisterToken(UserInfo userInfo);

    ErrorCode verifyRegisterToken(String token);
}
