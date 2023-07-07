package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceOperatorProvider {
    <T extends SystemResourceOperator> T
    getSystemResourceOperator(SystemResource systemResource);

    <T extends SystemResourceOperator> T
    getSystemResourceOperator(SystemResource systemResource, boolean checkDelete);

    <T extends SystemResourceOperator> T
    getSystemResourceOperator(SystemResource systemResource,
                              SystemResourceKind targetSystemResourceKind,
                              boolean checkDelete);

    <T extends SystemResourceOperator> T
    getSystemResourceOperator(SystemResource systemResource,
                              SystemResourceKind targetSystemResourceKind);
}
