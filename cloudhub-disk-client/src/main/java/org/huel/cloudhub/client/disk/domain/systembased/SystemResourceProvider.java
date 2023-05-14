package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceProvider {
    boolean supports(SystemResourceKind systemResourceKind);

    SystemResource provide(long resourceId,
                           SystemResourceKind systemResourceKind);
}
