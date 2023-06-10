package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceOperatorFactory extends SystemResourceSupportable {
    @Override
    boolean supports(SystemResourceKind systemResourceKind);

    boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz);

    SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            boolean checkDelete);
}
