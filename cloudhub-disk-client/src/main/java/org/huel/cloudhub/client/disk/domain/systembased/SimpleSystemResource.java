package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public record SimpleSystemResource(
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
