package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceLocator {
    SystemResource locate(long resourceId,
                          SystemResourceKind systemResourceKind);
}
