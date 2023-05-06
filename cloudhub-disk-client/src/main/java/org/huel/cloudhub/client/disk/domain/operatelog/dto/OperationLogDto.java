package org.huel.cloudhub.client.disk.domain.operatelog.dto;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.OperateType;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogAssociation;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public record OperationLogDto(
        long operatorId,
        long resourceId,
        SystemResourceKind resourceKind,
        long typeId,
        Action action,
        String name,
        String description,
        String address,
        long timestamp,
        String originContent,
        String changedContent,
        boolean isAssociated
) {

    public static OperationLogDto from(OperationLog operationLog,
                                       OperateType operateType) {
        if (operationLog == null) {
            return null;
        }
        return new OperationLogDto(
                operationLog.getOperator(),
                operationLog.getOperateResourceId(),
                operationLog.getSystemResourceKind(),
                operationLog.getOperateType(),
                operationLog.getAction(),
                operateType.getName(),
                operateType.getDescription(),
                operationLog.getAddress(),
                operationLog.getOperateTime(),
                operationLog.getOriginContent(),
                operationLog.getChangedContent(),
                false
        );
    }

    public static OperationLogDto from(OperationLog operationLog,
                                       OperationLogAssociation operationLogAssociation,
                                       OperateType operateType) {
        if (operationLog == null || operationLogAssociation == null) {
            return null;
        }
        return new OperationLogDto(
                operationLog.getOperator(),
                operationLogAssociation.getResourceId(),
                operationLogAssociation.getResourceKind(),
                operationLog.getOperateType(),
                operationLog.getAction(),
                operateType.getName(),
                operateType.getDescription(),
                operationLog.getAddress(),
                operationLog.getOperateTime(),
                operationLog.getOriginContent(),
                operationLog.getChangedContent(),
                true
        );
    }
}
