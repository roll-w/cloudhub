package org.huel.cloudhub.client.disk.controller.operation;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogCountProvider;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationService;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.operatelog.vo.OperationLogVo;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceException;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.service.UserManageService;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.client.disk.system.pages.PageableInterceptor;
import org.huel.cloudhub.web.DataErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class OperationLogManageController {
    private final OperationService operationService;
    private final OperationLogCountProvider operationLogCountProvider;
    private final UserSearchService userSearchService;
    private final UserManageService userManageService;
    private final PageableInterceptor pageableInterceptor;

    public OperationLogManageController(OperationService operationService,
                                        OperationLogCountProvider operationLogCountProvider,
                                        UserSearchService userSearchService,
                                        UserManageService userManageService,
                                        PageableInterceptor pageableInterceptor) {
        this.operationService = operationService;
        this.operationLogCountProvider = operationLogCountProvider;
        this.userSearchService = userSearchService;
        this.userManageService = userManageService;
        this.pageableInterceptor = pageableInterceptor;
    }


    @GetMapping("/{systemResourceKind}/{systemResourceId}/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogs(
            @PathVariable("systemResourceKind") String kind,
            @PathVariable("systemResourceId") Long systemResourceId,
            Pageable pageable) {
        SystemResourceKind systemResourceKind =
                SystemResourceKind.from(kind);
        if (systemResourceKind == null) {
            throw new SystemResourceException(DataErrorCode.ERROR_DATA_NOT_EXIST,
                    "Not found system resource kind: " + kind);
        }
        List<OperationLogDto> operationLogDtos = operationService.getOperationsByResource(
                new SimpleSystemResource(systemResourceId, systemResourceKind)
        );
        List<Long> userIds = operationLogDtos.stream()
                .map(OperationLogDto::operatorId)
                .distinct()
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);
        List<OperationLogVo> results = OperationLogVoUtils.convertToVo(
                operationLogDtos, attributedUsers);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results,
                        pageable,
                        () -> operationLogCountProvider.getOperationLogCount(
                                systemResourceId,
                                systemResourceKind
                        )
                )
        );
    }

    @GetMapping("/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogs(
            Pageable pageable) {
        List<OperationLogDto> operationLogDtos =
                operationService.getOperations(pageable);
        List<Long> userIds = operationLogDtos.stream()
                .map(OperationLogDto::operatorId)
                .distinct()
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);
        List<OperationLogVo> results = OperationLogVoUtils.convertToVo(
                operationLogDtos, attributedUsers);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results,
                        pageable,
                        OperationLog.class
                )
        );
    }


    @GetMapping("/user/{userId}/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogsByUser(
            @PathVariable("userId") Long userId,
            Pageable pageable) {
        // current user
        AttributedUser attributedUser = userManageService.getUser(userId);
        List<OperationLogDto> operationLogDtos = operationService.getOperationsByUserId(
                userId,
                pageable
        );
        List<? extends AttributedUser> attributedUsers =
                List.of(attributedUser);
        List<OperationLogVo> results = OperationLogVoUtils.convertToVo(
                operationLogDtos, attributedUsers);

        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results, pageable,
                        () -> operationLogCountProvider.getOperationLogCount(
                                attributedUser.getOperatorId()
                        )
                )
        );
    }
}
