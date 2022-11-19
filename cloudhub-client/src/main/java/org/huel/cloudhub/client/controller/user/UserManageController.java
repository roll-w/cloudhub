package org.huel.cloudhub.client.controller.user;

import org.huel.cloudhub.client.data.dto.user.UserCreateRequest;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.client.service.user.UserManageService;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Cheng
 */
@RestController
@UserAdminApi
public class UserManageController {
    private final UserManageService userManageService;

    public UserManageController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    @PutMapping("/create")
    public HttpResponseEntity<UserInfo> create(
            @RequestBody UserCreateRequest userCreateRequest) {
        // 参数比较多的时候，换成RequestBody
        if (userCreateRequest.discardEmail() == null) {
            var res = userManageService.createUser(
                    userCreateRequest.username(),
                    userCreateRequest.password(),
                    userCreateRequest.email());
            return HttpResponseEntity.create(res.toResponseBody());
        }
        var res = userManageService.createUser(
                userCreateRequest.username(),
                userCreateRequest.password(),
                userCreateRequest.email(),
                userCreateRequest.role(),
                userCreateRequest.discardEmail());
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @PostMapping("/delete")
    //@DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(@RequestParam Map<String, String> map) {
        map.get("userId");
        long userId = Long.parseLong(map.get("userId"));
        var res =
                userManageService.deleteUser(userId);
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @GetMapping("/get")
    public HttpResponseEntity<UserInfo> getUser(@RequestParam String username) {
        User user = userManageService.queryUser(username);
        if (user == null) {
            return HttpResponseEntity.failure("User not exist",
                    ErrorCode.ERROR_USER_NOT_EXIST, (UserInfo) null);
        }
        return HttpResponseEntity.success(user.toInfo());
    }

    @GetMapping("/get/all")
    public HttpResponseEntity<List<UserInfo>> getAllUsers() {
        List<UserInfo> userInfos = userManageService.getUsers();
        return HttpResponseEntity.success(userInfos);
    }
}
