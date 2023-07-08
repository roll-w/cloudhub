package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.common.ParamValidate;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.PairParameterRequest;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThread;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperatorProvider;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.LoginLogService;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.user.UserOperator;
import org.huel.cloudhub.client.disk.domain.user.dto.LoginLog;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.client.disk.domain.user.vo.UserCommonDetailsVo;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Page;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class UserController {
    private final UserSearchService userSearchService;
    private final LoginLogService loginLogService;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;

    public UserController(UserSearchService userSearchService,
                          LoginLogService loginLogService,
                          SystemResourceOperatorProvider systemResourceOperatorProvider,
                          ContextThreadAware<PageableContext> pageableContextThreadAware) {
        this.userSearchService = userSearchService;
        this.loginLogService = loginLogService;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
        this.pageableContextThreadAware = pageableContextThreadAware;
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

    @PutMapping("/user")
    public HttpResponseEntity<Void> updateUserInfo(
            @RequestBody UserUpdateRequest request
    ) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();

        UserOperator userOperator = systemResourceOperatorProvider
                .getSystemResourceOperator(userIdentity, true);
        userOperator.disableAutoUpdate()
                .setNickname(request.nickname())
                .setEmail(request.email())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/user/password")
    public HttpResponseEntity<Void> resetUserPassword(
            PairParameterRequest<String, String> request
    ) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        UserOperator userOperator = systemResourceOperatorProvider
                .getSystemResourceOperator(userIdentity, true);
        userOperator.disableAutoUpdate()
                .setPassword(request.first(), request.second())
                .update();
        return HttpResponseEntity.success();
    }

    @GetMapping("/users/{userId}")
    public HttpResponseEntity<UserCommonDetailsVo> getUserInfo(
            @PathVariable("userId") Long userId) {
        AttributedUser attributedUser = userSearchService.findUser(userId);
        return HttpResponseEntity.success(
                UserCommonDetailsVo.of(attributedUser)
        );
    }

    @GetMapping("/users/search")
    public HttpResponseEntity<List<UserCommonDetailsVo>> searchUsers(
            @RequestParam("keyword") String keyword) {
        ParamValidate.notEmpty(keyword, "keyword");
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        pageableContext.setIncludeDeleted(false);

        List<AttributedUser> attributedUsers =
                userSearchService.findUsers(keyword);

        List<UserCommonDetailsVo> userCommonDetailsVos =
                attributedUsers.stream()
                        .map(UserCommonDetailsVo::of)
                        .toList();
        return HttpResponseEntity.success(
                pageableContext.toPage(userCommonDetailsVos)
        );
    }

    @GetMapping("/user/login/logs")
    public HttpResponseEntity<List<LoginLog>> getUserLoginLogs(Pageable pageable) {
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
