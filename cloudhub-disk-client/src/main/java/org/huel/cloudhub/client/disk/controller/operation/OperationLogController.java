package org.huel.cloudhub.client.disk.controller.operation;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.common.CloudhubBizRuntimeException;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationService;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.web.DataErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class OperationLogController {
    private final OperationService operationService;

    public OperationLogController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/{systemResourceKind}/{systemResourceId}/operations/logs")
    public HttpResponseEntity<List<OperationLogDto>> getOperationLogs(
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

        return HttpResponseEntity.success(operationLogDtos);
    }


    @GetMapping("/user/operations/logs")
    public HttpResponseEntity<List<OperationLogDto>> getOperationLogsByUser() {
        // current user
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        List<OperationLogDto> operationLogDtos =
                operationService.getOperationsByUserId(userIdentity.getUserId());
        return HttpResponseEntity.success(operationLogDtos);
    }


}
