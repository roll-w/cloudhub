package org.huel.cloudhub.objectstorage.controller.user;

import org.apache.commons.lang3.StringUtils;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.huel.cloudhub.objectstorage.data.dto.user.UserLoginRequest;
import org.huel.cloudhub.objectstorage.service.user.UserService;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.MessagePackage;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.UserErrorCode;
import org.huel.cloudhub.web.WebCommonErrorCode;
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
            throw new BusinessRuntimeException(WebCommonErrorCode.ERROR_PARAM_MISSING,
                    "Username cannot be empty.");
        }
        if (StringUtils.isEmpty(userLoginRequest.password())) {
            throw new BusinessRuntimeException(WebCommonErrorCode.ERROR_PARAM_MISSING,
                    "Password cannot be empty.");
        }

        MessagePackage<UserInfo> infoMessagePackage =
                userService.loginByUsername(request, userLoginRequest.username(), userLoginRequest.password());
        return HttpResponseEntity.of(infoMessagePackage.toResponseBody());
    }

    @GetMapping("/current")
    public HttpResponseEntity<UserInfo> current(HttpServletRequest request) {
        UserInfo userInfo = userService.getCurrentUser(request);
        if (userInfo == null) {
            throw new BusinessRuntimeException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }
        return HttpResponseEntity.success(userInfo);
    }

    @PostMapping("/logout")
    public HttpResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return HttpResponseEntity.success();
    }
}
