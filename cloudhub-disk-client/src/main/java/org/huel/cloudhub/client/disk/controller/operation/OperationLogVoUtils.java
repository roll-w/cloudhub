package org.huel.cloudhub.client.disk.controller.operation;

import org.huel.cloudhub.client.disk.common.CloudhubBizRuntimeException;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.operatelog.vo.OperationLogVo;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.web.DataErrorCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public final class OperationLogVoUtils {

    public static List<OperationLogVo> convertToVo(List<OperationLogDto> operationLogDtos,
                                                   List<? extends AttributedUser> attributedUsers) {
        List<OperationLogVo> results = new ArrayList<>();
        for (OperationLogDto operationLogDto : operationLogDtos) {
            AttributedUser attributedUser = binarySearch(operationLogDto.operatorId(), attributedUsers);
            results.add(OperationLogVo.of(operationLogDto, attributedUser));
        }
        return results;
    }


    private static AttributedUser binarySearch(long userId,
                                               List<? extends AttributedUser> attributedUsers) {
        AttributedUser attributedUser =
                UserSearchService.binarySearch(userId, attributedUsers);
        if (attributedUser == null) {
            throw new CloudhubBizRuntimeException(DataErrorCode.ERROR_DATA_NOT_EXIST,
                    "Not found user: " + userId);
        }
        return attributedUser;
    }


    private OperationLogVoUtils() {
    }
}
