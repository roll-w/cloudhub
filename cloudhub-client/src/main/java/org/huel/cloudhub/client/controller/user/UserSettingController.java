package org.huel.cloudhub.client.controller.user;

import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.client.service.user.UserSettingService;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Cheng
 */
@RestController
public class UserSettingController {
//    @Autowirde为啥不用

    private UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService){
        this.userSettingService = userSettingService;
    }
//    更新密码
    @PostMapping("/resetPassword")
    public HttpResponseEntity<UserInfo> resetPassword(@RequestParam Map<String,String> map){
        Long usrId = Long.valueOf(map.get("userId"));
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");
        userSettingService.resetPassword(usrId,oldPassword,newPassword);
        return HttpResponseEntity.success();
    }
//  忘记密码
    @PostMapping("/lostPassword")
    public HttpResponseEntity<UserInfo> lostPassword(@RequestParam Map<String,String> map){
        Long usrId = Long.valueOf(map.get("userId"));
        String newPassword = map.get("newPassword");
        userSettingService.resetPassword(usrId,newPassword);
        return HttpResponseEntity.success();
    }
//    更新用户名
    @PostMapping("/updateUsername")
    public HttpResponseEntity<UserInfo> resetUsername(@RequestParam Map<String,String> map){
        Long usrId = Long.valueOf(map.get("userId"));
        String newUsername = map.get("newUsername");
        userSettingService.resetUsername(usrId,newUsername);
        return HttpResponseEntity.success();
    }
}
