package org.huel.cloudhub.client.controller.user;

import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.user.UserService;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.common.MessagePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RollW
 */
@RestController
@UserApi
public class UserVerifyController {
    private final UserService userService;

    private final Logger logger =
            LoggerFactory.getLogger(UserVerifyController.class);

    public UserVerifyController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 确认账户激活验证令牌
     *
     * @param token 激活令牌
     * @return {@link HttpResponseEntity}
     */
    @GetMapping(value = "/register/confirm/{token}")
    public HttpResponseEntity<UserInfo> confirmRegister(
            @PathVariable(value = "token") String token) {
        MessagePackage<UserInfo> infoMessagePackage =
                userService.verifyToken(token);

        return HttpResponseEntity.create(
                infoMessagePackage.toResponseBody());
    }

    /**
     * 重新发送账户激活验证令牌
     *
     * @param userId 用户ID
     * @return {@link HttpResponseEntity}
     */
    @GetMapping(value = "/register/resend/{id}")
    public HttpResponseEntity<Void> resendRegisterToken(
            @PathVariable(value = "id") long userId) {
        MessagePackage<Void> tokenMessagePackage =
                userService.resendToken(userId);
        return HttpResponseEntity.create(
                tokenMessagePackage.toResponseBody());
    }

}
