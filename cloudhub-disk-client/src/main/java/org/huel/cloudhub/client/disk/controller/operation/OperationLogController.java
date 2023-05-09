package org.huel.cloudhub.client.disk.controller.operation;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.common.CloudhubBizRuntimeException;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationService;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.operatelog.vo.OperationLogVo;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.client.disk.system.pages.PageableInterceptor;
import org.huel.cloudhub.web.DataErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Api
public class OperationLogController {
    private final OperationService operationService;
    private final UserSearchService userSearchService;
    private final PageableInterceptor pageableInterceptor;

    public OperationLogController(OperationService operationService,
                                  UserSearchService userSearchService,
                                  PageableInterceptor pageableInterceptor) {
        this.operationService = operationService;
        this.userSearchService = userSearchService;
        this.pageableInterceptor = pageableInterceptor;
    }

    @GetMapping("/{systemResourceKind}/{systemResourceId}/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogs(
            @PathVariable("systemResourceKind") String kind,
            @PathVariable("systemResourceId") Long systemResourceId) {
        SystemResourceKind systemResourceKind =
                SystemResourceKind.from(kind);
        if (systemResourceKind == null) {
            throw new CloudhubBizRuntimeException(DataErrorCode.ERROR_DATA_NOT_EXIST,
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
        List<OperationLogVo> results = convertToVo(operationLogDtos, attributedUsers);
        return HttpResponseEntity.success(results);
    }


    @GetMapping("/user/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogsByUser(Pageable pageable) {
        // current user
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        List<OperationLogDto> operationLogDtos =
                operationService.getOperationsByUserId(userIdentity.getUserId());
        List<Long> userIds = operationLogDtos.stream()
                .map(OperationLogDto::operatorId)
                .distinct()
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);
        List<OperationLogVo> results = convertToVo(operationLogDtos, attributedUsers);


        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results, pageable,
                        OperationLog.class
                )
        );
    }


    private List<OperationLogVo> convertToVo(List<OperationLogDto> operationLogDtos,
                                             List<? extends AttributedUser> attributedUsers) {
        List<OperationLogVo> results = new ArrayList<>();
        for (OperationLogDto operationLogDto : operationLogDtos) {
            AttributedUser attributedUser = binarySearch(operationLogDto.operatorId(), attributedUsers);
            results.add(OperationLogVo.of(operationLogDto, attributedUser));
        }
        return results;
    }

    private AttributedUser binarySearch(long userId,
                                        List<? extends AttributedUser> attributedUsers) {
        AttributedUser attributedUser =
                UserSearchService.binarySearch(userId, attributedUsers);
        if (attributedUser == null) {
            throw new CloudhubBizRuntimeException(DataErrorCode.ERROR_DATA_NOT_EXIST,
                    "Not found user: " + userId);
        }
        return attributedUser;
    }

}
