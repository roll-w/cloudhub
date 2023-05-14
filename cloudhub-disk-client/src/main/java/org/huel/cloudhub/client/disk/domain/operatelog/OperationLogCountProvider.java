package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public interface OperationLogCountProvider {
    long getOperationLogCount(long operatorId);

    long getOperationLogCount(long resourceId, SystemResourceKind resourceKind);
}
