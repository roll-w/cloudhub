package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceProvider extends SystemResourceSupportable {
    @Override
    boolean supports(SystemResourceKind systemResourceKind);

    SystemResource provide(long resourceId,
                           SystemResourceKind systemResourceKind);
}
