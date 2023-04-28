package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;

import java.util.List;

/**
 * @author RollW
 */
public interface OperateLogger {
    void recordOperate(Action action, SystemResource systemResource,
                       OperateType operateType,
                       String originContent,
                       String changedContent,
                       List<SystemResource> associateResources);

    void recordOperate(Action action, SystemResource systemResource,
                       OperateType operateType,
                       String originContent, List<SystemResource> associateResources);

    void recordOperate(Action action, SystemResource systemResource,
                       OperateType operateType,
                       List<SystemResource> associateResources);

    void recordOperate(Operation operation);
}
