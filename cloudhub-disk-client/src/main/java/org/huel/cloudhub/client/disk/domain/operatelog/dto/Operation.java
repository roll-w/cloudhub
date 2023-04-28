package org.huel.cloudhub.client.disk.domain.operatelog.dto;

import org.huel.cloudhub.client.disk.domain.operatelog.OperateType;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;

import java.util.List;

/**
 * @author RollW
 */
public record Operation(
        Operator operator,
        SystemResource systemResource,
        OperateType operateType,
        String address,
        long timestamp,
        String originContent,
        String changedContent,
        List<SystemResource> associatedResources
) {

    public Operation(Operator operator,
                     SystemResource systemResource,
                     OperateType operateType,
                     String address,
                     long timestamp,
                     String originContent,
                     String changedContent) {
        this(operator, systemResource, operateType,
                address, timestamp, originContent,
                changedContent,
                null
        );
    }
}
