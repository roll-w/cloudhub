package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;

/**
 * @author RollW
 */
public interface OperationService {
    OperationLog recordOperation(Operation operation);
}
