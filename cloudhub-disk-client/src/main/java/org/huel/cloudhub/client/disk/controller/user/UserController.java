package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.LoginLogService;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.user.dto.LoginLog;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.client.disk.domain.user.vo.UserCommonDetailsVo;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Page;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class UserController {
    private final UserSearchService userSearchService;
    private final LoginLogService loginLogService;

    public UserController(UserSearchService userSearchService,
                          LoginLogService loginLogService) {
        this.userSearchService = userSearchService;
        this.loginLogService = loginLogService;
    }

    @GetMapping("/user")
    public HttpResponseEntity<UserCommonDetailsVo> getAuthenticatedUser() {
        ApiContextHolder.ApiContext context = ApiContextHolder.getContext();
        UserIdentity userInfo = context.userInfo();
        if (userInfo == null) {
            throw new BusinessRuntimeException(AuthErrorCode.ERROR_UNAUTHORIZED_USE);
        }
        AttributedUser attributedUser =
                userSearchService.findUser(userInfo.getUserId());
        return HttpResponseEntity.success(
                UserCommonDetailsVo.of(attributedUser)
        );
    }

    @GetMapping("/user/{userId}")
    public HttpResponseEntity<UserCommonDetailsVo> getUserInfo(
            @PathVariable("userId") Long userId) {
        UserIdentity userIdentity = userSearchService.findUser(userId);
        return HttpResponseEntity.success(
                UserCommonDetailsVo.of(userIdentity)
        );
    }

    @GetMapping("/user/login/logs")
    public HttpResponseEntity<List<LoginLog>> getUserLoginLogs(
            Pageable pageable) {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        if (userIdentity == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_INVALID_TOKEN);
        }
        List<LoginLog> loginLogs =
                loginLogService.getUserLogs(userIdentity.getUserId(), pageable);
        return HttpResponseEntity.success(Page.of(
                pageable,
                loginLogService.getUserLogsCount(userIdentity.getUserId()),
                loginLogs
        ));
    }
}
