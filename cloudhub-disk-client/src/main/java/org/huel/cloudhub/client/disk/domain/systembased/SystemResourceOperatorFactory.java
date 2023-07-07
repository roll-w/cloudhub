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
            boolean checkDelete
    );

    default SystemResourceOperator createResourceOperator(SystemResource systemResource) {
        return createResourceOperator(systemResource, true);
    }

    /**
     * Open a new resource operator through its related resource.
     * Such as use Storage to open an operator of StoragePermission.
     * <p>
     * Will check the target system resource kind is supported by
     * this factory.
     */
    default SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind,
            boolean checkDelete
    ) {
        throw new UnsupportedOperationException();
    }

    default SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind
    ) {
        return createResourceOperator(systemResource, targetSystemResourceKind, true);
    }
}
