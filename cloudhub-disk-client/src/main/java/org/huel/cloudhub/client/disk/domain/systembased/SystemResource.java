package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResource extends SystemResourceKind.Kind {
    long getResourceId();

    @Override
    SystemResourceKind getSystemResourceKind();
}