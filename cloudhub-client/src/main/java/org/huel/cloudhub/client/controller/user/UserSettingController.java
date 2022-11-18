package org.huel.cloudhub.client.controller.user;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.user.UserSettingService;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Cheng
 */
@RestController
@UserSettingApi
public class UserSettingController {
    private final UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    //    更新密码
    @PostMapping("/password/reset")
    public HttpResponseEntity<UserInfo> resetPassword(@RequestParam Map<String, String> map) {
        String userIdParam = map.get("userId");
        Validate.notEmpty(userIdParam, "User id null.");
        long usrId = Long.parseLong(userIdParam);

        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");
        var res = userSettingService
                .resetPassword(usrId, oldPassword, newPassword);

        return HttpResponseEntity.create(res.toResponseBody());
    }

    //  忘记密码
    @PostMapping("/password/lost")
    public HttpResponseEntity<UserInfo> lostPassword(@RequestParam Map<String, String> map) {
        String userIdParam = map.get("userId");
        Validate.notEmpty(userIdParam, "User id null.");
        long userId = Long.parseLong(userIdParam);
        String newPassword = map.get("newPassword");
        var res =
                userSettingService.resetPassword(userId, newPassword);
        return HttpResponseEntity.create(res.toResponseBody());
    }

    //    更新用户名
    @PostMapping("/username")
    public HttpResponseEntity<UserInfo> resetUsername(@RequestParam Map<String, String> map) {
        String userIdParam = map.get("userId");
        Validate.notEmpty(userIdParam, "User id null.");
        long userId = Long.parseLong(userIdParam);
        String newUsername = map.get("newUsername");

        var res =
                userSettingService.resetUsername(userId, newUsername);
        return HttpResponseEntity.create(res.toResponseBody());
    }
}
