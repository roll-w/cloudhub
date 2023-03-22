package org.huel.cloudhub.client.controller.user;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.data.dto.user.UserCreateRequest;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.client.service.user.UserManageService;
import org.huel.cloudhub.common.BusinessRuntimeException;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.UserErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        ValidateHelper.validateUserAdmin(request, userGetter);

        // 参数比较多的时候，换成RequestBody
        if (userCreateRequest.discardEmail() == null) {
            var res = userManageService.createUser(
                    userCreateRequest.username(),
                    userCreateRequest.password(),
                    userCreateRequest.email());
            return HttpResponseEntity.of(res.toResponseBody());
        }
        var res = userManageService.createUser(
                userCreateRequest.username(),
                userCreateRequest.password(),
                userCreateRequest.email(),
                userCreateRequest.role(),
                userCreateRequest.discardEmail());
        return HttpResponseEntity.of(res.toResponseBody());
    }

    @PostMapping("/delete")
    //@DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(HttpServletRequest request, @RequestParam Map<String, String> map) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        String userIdParam = map.get("userId");
        Validate.notNull(userIdParam, "userId cannot be null.");

        long userId = Long.parseLong(userIdParam);
        var res =
                userManageService.deleteUser(userId);
        return HttpResponseEntity.of(res.toResponseBody());
    }

    @GetMapping("/get")
    public HttpResponseEntity<UserInfo> getUser(@RequestParam String username) {
        User user = userManageService.queryUser(username);
        if (user == null) {
            throw new BusinessRuntimeException(UserErrorCode.ERROR_USER_NOT_EXIST);
        }
        return HttpResponseEntity.success(user.toInfo());
    }

    @GetMapping("/get/all")
    public HttpResponseEntity<List<UserInfo>> getAllUsers(HttpServletRequest request) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        List<UserInfo> userInfos = userManageService.getUsers();
        return HttpResponseEntity.success(userInfos);
    }
}
