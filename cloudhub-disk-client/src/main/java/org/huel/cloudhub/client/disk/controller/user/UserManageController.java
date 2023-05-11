package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.domain.user.LoginLogService;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.dto.LoginLog;
import org.huel.cloudhub.client.disk.domain.user.service.UserManageService;
import org.huel.cloudhub.client.disk.domain.user.vo.UserDetailsVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Page;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class UserManageController {
    private final UserManageService userManageService;
    private final LoginLogService loginLogService;

    public UserManageController(UserManageService userManageService,
                                LoginLogService loginLogService) {
        this.userManageService = userManageService;
        this.loginLogService = loginLogService;
    }

    @GetMapping("/users")
    public HttpResponseEntity<List<UserDetailsVo>> getUserList(Pageable pageRequest) {
        List<User> userIdentities = userManageService.getUsers(
                pageRequest.getPage(),
                pageRequest.getSize()
        );
        return HttpResponseEntity.success(
                userIdentities.stream().map(
                        UserDetailsVo::of
                ).toList()
        );
    }

    @GetMapping("/users/{userId}")
    public HttpResponseEntity<UserDetailsVo> getUserDetails(
            @PathVariable Long userId) {
        User user = userManageService.getUser(userId);
        UserDetailsVo userDetailsVo =
                UserDetailsVo.of(user);
        return HttpResponseEntity.success(userDetailsVo);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable String userId) {

    }

    @PutMapping("/users/{userId}")
    public void updateUser(@PathVariable String userId) {

    }

    @PostMapping("/users")
    public void createUser() {

    }

    @GetMapping("/users/login/logs")
    public HttpResponseEntity<List<LoginLog>> getLoginLogs(
            Pageable pageable) {
        List<LoginLog> loginLogs = loginLogService.getLogs(pageable);
        long count = loginLogService.getLogsCount();

        return HttpResponseEntity.success(
                Page.of(pageable, count, loginLogs)
        );
    }

}
