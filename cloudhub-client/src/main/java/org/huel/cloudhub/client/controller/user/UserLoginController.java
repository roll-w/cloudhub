package org.huel.cloudhub.client.controller.user;

import org.apache.commons.lang3.StringUtils;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.dto.user.UserLoginRequest;
import org.huel.cloudhub.client.service.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
@UserApi
@RestController
public class UserLoginController {
    private final UserService userService;

    public UserLoginController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpResponseEntity<UserInfo> login(HttpServletRequest request,
                                              @RequestBody UserLoginRequest userLoginRequest) {
        if (StringUtils.isEmpty(userLoginRequest.username())) {
            return HttpResponseEntity.failure(
                    "Username cannot be empty.",
                    ErrorCode.ERROR_PARAM_MISSING);
        }
        if (StringUtils.isEmpty(userLoginRequest.password())) {
            return HttpResponseEntity.failure(
                    "Password cannot be empty.",
                    ErrorCode.ERROR_PARAM_MISSING);
        }

        MessagePackage<UserInfo> infoMessagePackage =
                userService.loginByUsername(request, userLoginRequest.username(), userLoginRequest.password());
        return HttpResponseEntity.create(infoMessagePackage.toResponseBody());
    }

    @PostMapping("/test")
    public HttpResponseEntity<UserInfo> test(@RequestBody UserLoginRequest request) {
        return HttpResponseEntity.success(request.toString());
    }

    @GetMapping("/current")
    public HttpResponseEntity<UserInfo> current(HttpServletRequest request) {
        UserInfo userInfo = userService.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure(
                    "No user login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN,
                    null);
        }
        return HttpResponseEntity.success(userInfo);
    }

    @GetMapping("/logout")
    public HttpResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return HttpResponseEntity.success();
    }


    @GetMapping("/message")
    public HttpResponseEntity<String> needsLoginFirst() {
        return HttpResponseEntity.failure("need login first.",
                ErrorCode.ERROR_USER_NOT_LOGIN,
                "need login first");
    }

}
