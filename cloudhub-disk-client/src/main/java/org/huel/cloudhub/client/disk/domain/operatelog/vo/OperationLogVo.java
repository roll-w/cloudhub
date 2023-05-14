package org.huel.cloudhub.client.disk.domain.operatelog.vo;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;

/**
 * @author RollW
 */
public record OperationLogVo(
        long id,
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
        long associatedTo
) {



    public static OperationLogVo of(OperationLogDto operationLogDto) {
        return new OperationLogVo(
                operationLogDto.id(),
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
                operationLogDto.associatedTo()
        );
    }

    public static OperationLogVo of(OperationLogDto operationLogDto,
                                    AttributedUser attributedUser) {
        return new OperationLogVo(
                operationLogDto.id(),
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
                operationLogDto.associatedTo()
        );
    }
}
