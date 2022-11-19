package org.huel.cloudhub.client.controller.user;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.data.dto.user.UserCreateRequest;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.client.service.user.UserManageService;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Cheng
 */
@RestController
@UserAdminApi
public class UserManageController {
    private final UserManageService userManageService;
    private final UserGetter userGetter;

    public UserManageController(UserManageService userManageService,
                                UserGetter userGetter) {
        this.userManageService = userManageService;
        this.userGetter = userGetter;
    }

    @PutMapping("/create")
    public HttpResponseEntity<UserInfo> create(
            HttpServletRequest request,
            @RequestBody UserCreateRequest userCreateRequest) {
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<UserInfo>) httpResponse;
        }

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
    public HttpResponseEntity<Void> delete(HttpServletRequest request, @RequestParam Map<String, String> map) {
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<Void>) httpResponse;
        }
        String userIdParam = map.get("userId");
        Validate.notNull(userIdParam, "userId cannot be null.");

        long userId = Long.parseLong(userIdParam);
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
    public HttpResponseEntity<List<UserInfo>> getAllUsers(HttpServletRequest request) {
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<List<UserInfo>>) httpResponse;
        }

        List<UserInfo> userInfos = userManageService.getUsers();
        return HttpResponseEntity.success(userInfos);
    }

    private HttpResponseEntity<?> validate(HttpServletRequest request) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        if (!userInfo.role().hasPrivilege()) {
            return HttpResponseEntity.failure("User has no permissions.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        return null;
    }
}
