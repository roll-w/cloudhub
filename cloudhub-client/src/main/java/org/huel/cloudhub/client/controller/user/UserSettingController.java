package org.huel.cloudhub.client.controller.user;

import org.huel.cloudhub.client.data.dto.user.UserCreateRequest;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.client.service.user.UserSettingService;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;


/**
 * @author Cheng
 */
@RestController
@UserSettingApi
public class UserSettingController {
    private final UserSettingService userSettingService;

    private final UserGetter userGetter;

    public UserSettingController(UserSettingService userSettingService,UserGetter userGetter) {
        this.userSettingService = userSettingService;
        this.userGetter = userGetter;
    }

    //    更新密码，首先判断是否登录和获取老密码,更改userInfo
    @PostMapping("/password/reset")
    public HttpResponseEntity<UserInfo> resetPassword(
            HttpServletRequest request,
            @RequestBody UserCreateRequest userCreateRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }

        var res = userSettingService
                .resetPassword(
                        userInfo.id(),
                        userInfo.password(),
                        userCreateRequest.password());

        return HttpResponseEntity.create(res.toResponseBody());
    }

    //  忘记密码,首先判断用户是否存在
    @PostMapping("/password/lost")
    public HttpResponseEntity<UserInfo> lostPassword(
            HttpServletRequest request,
            @RequestBody UserCreateRequest userCreateRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
//        使用userInfo.id判断空值会警告
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not exit.",
                    ErrorCode.ERROR_USER_NOT_EXIST);
        }
        var res =
                userSettingService.resetPassword(userInfo.id(),
                        userCreateRequest.password());
        return HttpResponseEntity.create(res.toResponseBody());
    }

    //    更新用户名
    @PostMapping("/username")
    public HttpResponseEntity<UserInfo> resetUsername(
            HttpServletRequest request,
            @RequestBody UserCreateRequest userCreateRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        var res =
                userSettingService.resetUsername(userInfo.id(),
                       userCreateRequest.username());
        return HttpResponseEntity.create(res.toResponseBody());
    }
}
