package org.huel.cloudhub.client.disk.domain.operatelog.dto;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public record OperationAssociation(
        long resourceId,
        SystemResourceKind systemResourceKind
) implements SystemResource {
    @Override
    public long getResourceId() {
        return resourceId;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return systemResourceKind;
    }
}
