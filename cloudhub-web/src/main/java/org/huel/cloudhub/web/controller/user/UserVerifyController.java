package org.huel.cloudhub.web.controller.user;

import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.web.data.dto.UserInfo;
import org.huel.cloudhub.web.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author RollW
 */
@Controller
@UserApi
public class UserVerifyController {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    private final Logger logger =
            LoggerFactory.getLogger(UserVerifyController.class);

    public UserVerifyController(UserService userService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
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
