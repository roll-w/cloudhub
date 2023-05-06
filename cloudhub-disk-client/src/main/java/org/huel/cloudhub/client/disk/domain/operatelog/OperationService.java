package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;

import java.util.List;

/**
 * @author RollW
 */
public interface OperationService {
    void recordOperation(Operation operation);

    List<OperationLogDto> getOperationsByUserId(long userId);

    List<OperationLogDto> getOperationsByResource(SystemResource systemResource);
}
