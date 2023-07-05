package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * Define the system resource.
 *
 * @author RollW
 */
public interface SystemResource extends SystemResourceKind.Kind, Castable {
    long getResourceId();

    @Override
    SystemResourceKind getSystemResourceKind();
}
