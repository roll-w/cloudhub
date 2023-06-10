package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceSupportable {
    boolean supports(SystemResourceKind systemResourceKind);
}
