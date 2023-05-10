package org.huel.cloudhub.client.disk.domain.operatelog.vo;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.OperateType;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogAssociation;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;

/**
 * @author RollW
 */
public record OperationLogVo(
        long operatorId,
        String username,
        String nickname,
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

    public static OperationLogVo from(OperationLog operationLog,
                                      OperateType operateType) {
        if (operationLog == null) {
            return null;
        }
        return new OperationLogVo(
                operationLog.getOperator(),
                null, null,
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

    public static OperationLogVo from(OperationLog operationLog,
                                      OperateType operateType,
                                      AttributedUser attributedUser) {
        if (operationLog == null) {
            return null;
        }
        return new OperationLogVo(
                operationLog.getOperator(),
                attributedUser.getUsername(),
                attributedUser.getNickname(),
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

    public static OperationLogVo from(OperationLog operationLog,
                                      OperationLogAssociation operationLogAssociation,
                                      OperateType operateType) {
        if (operationLog == null || operationLogAssociation == null) {
            return null;
        }
        return new OperationLogVo(
                operationLog.getOperator(),
                null, null,
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

    public static OperationLogVo from(OperationLog operationLog,
                                      OperationLogAssociation operationLogAssociation,
                                      OperateType operateType,
                                      AttributedUser attributedUser) {
        if (operationLog == null || operationLogAssociation == null) {
            return null;
        }
        return new OperationLogVo(
                operationLog.getOperator(),
                attributedUser.getUsername(),
                attributedUser.getNickname(),
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

    public static OperationLogVo of(OperationLogDto operationLogDto) {
        return new OperationLogVo(
                operationLogDto.operatorId(),
                null,
                null,
                operationLogDto.resourceId(),
                operationLogDto.resourceKind(),
                operationLogDto.typeId(),
                operationLogDto.action(),
                operationLogDto.name(),
                operationLogDto.description(),
                operationLogDto.address(),
                operationLogDto.timestamp(),
                operationLogDto.originContent(),
                operationLogDto.changedContent(),
                operationLogDto.isAssociated()
        );
    }

    public static OperationLogVo of(OperationLogDto operationLogDto,
                                    AttributedUser attributedUser) {
        return new OperationLogVo(
                operationLogDto.operatorId(),
                attributedUser.getUsername(),
                attributedUser.getNickname(),
                operationLogDto.resourceId(),
                operationLogDto.resourceKind(),
                operationLogDto.typeId(),
                operationLogDto.action(),
                operationLogDto.name(),
                operationLogDto.description(),
                operationLogDto.address(),
                operationLogDto.timestamp(),
                operationLogDto.originContent(),
                operationLogDto.changedContent(),
                operationLogDto.isAssociated()
        );
    }
}
