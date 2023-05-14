package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationLogDto;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.web.data.page.Pageable;

import java.util.List;

/**
 * @author RollW
 */
public interface OperationService {
    void recordOperation(Operation operation);

    List<OperationLogDto> getOperations(Pageable pageable);

    List<OperationLogDto> getOperationsByUserId(long userId);

    List<OperationLogDto> getOperationsByUserId(long userId, Pageable pageable);

    List<OperationLogDto> getOperationsByResource(SystemResource systemResource);

    List<OperationLogDto> getOperationsByResource(SystemResource systemResource,
                                                  Pageable pageable);
}
