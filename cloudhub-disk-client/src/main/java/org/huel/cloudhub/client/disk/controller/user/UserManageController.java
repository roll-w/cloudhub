package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.controller.OneParameterRequest;
import org.huel.cloudhub.client.disk.controller.user.vo.UserCreateRequest;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperatorProvider;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.LoginLogService;
import org.huel.cloudhub.client.disk.domain.user.UserOperator;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class UserManageController {
    private final UserManageService userManageService;
    private final LoginLogService loginLogService;
    private final ContextThreadAware<PageableContext> pageableContextAware;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;

    public UserManageController(UserManageService userManageService,
                                LoginLogService loginLogService,
                                ContextThreadAware<PageableContext> pageableContextAware,
                                SystemResourceOperatorProvider systemResourceOperatorProvider) {
        this.userManageService = userManageService;
        this.loginLogService = loginLogService;
        this.pageableContextAware = pageableContextAware;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
    }

    @GetMapping("/users")
    public HttpResponseEntity<List<UserDetailsVo>> getUserList(Pageable pageRequest) {
        List<? extends AttributedUser> userIdentities = userManageService.getUsers(
                pageRequest
        );
        return HttpResponseEntity.success(
                userIdentities.stream().map(
                        UserDetailsVo::of
                ).toList()
        );
    }

    @GetMapping("/users/{id}")
    public HttpResponseEntity<UserDetailsVo> getUserDetails(
            @PathVariable("id") Long userId) {
        AttributedUser user = userManageService.getUser(userId);
        UserDetailsVo userDetailsVo =
                UserDetailsVo.of(user);
        return HttpResponseEntity.success(userDetailsVo);
    }

    @DeleteMapping("/users/{id}")
    public HttpResponseEntity<Void> deleteUser(
            @PathVariable("id") Long userId) {
        UserOperator userOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(userId, SystemResourceKind.USER),
                false
        );
        userOperator
                .enableAutoUpdate()
                .delete();

        return HttpResponseEntity.success();
    }

    @PutMapping("/users/{id}/password")
    public HttpResponseEntity<Void> resetUserPassword(
            @PathVariable("id") Long userId,
            @RequestBody OneParameterRequest<String> request) {
        UserOperator userOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(userId, SystemResourceKind.USER),
                false
        );
        userOperator.enableAutoUpdate()
                .setPassword(request.value())
                .update();
        return HttpResponseEntity.success();
    }


    @PutMapping("/users/{id}")
    public HttpResponseEntity<Void> updateUser(
            @PathVariable("id") Long userId,
            @RequestBody UserUpdateRequest userUpdateRequest) {
        UserOperator userOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(userId, SystemResourceKind.USER),
                false
        );
        userOperator.disableAutoUpdate()
                .setNickname(userUpdateRequest.nickname())
                .setEmail(userUpdateRequest.email())
                .setRole(userUpdateRequest.role())
                .setEnabled(userUpdateRequest.enabled())
                .setCanceled(userUpdateRequest.canceled())
                .setLocked(userUpdateRequest.locked())
                .update();

        return HttpResponseEntity.success();
    }

    @PostMapping("/users")
    public HttpResponseEntity<Void> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        userManageService.createUser(
                userCreateRequest.username(),
                userCreateRequest.password(),
                userCreateRequest.email(),
                userCreateRequest.role(),
                true
        );
        return HttpResponseEntity.success();
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
