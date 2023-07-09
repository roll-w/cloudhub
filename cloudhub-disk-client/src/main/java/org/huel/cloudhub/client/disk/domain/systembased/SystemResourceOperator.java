package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface SystemResourceOperator extends ByStatusProvider, Castable {
    /**
     * For some system resources that may not be updated automatically
     * (such as some batch operations), you need to call this method
     * to update.
     */
    SystemResource update() throws BusinessRuntimeException;

    SystemResource delete() throws BusinessRuntimeException;

    SystemResource rename(String newName) throws BusinessRuntimeException,
            UnsupportedOperationException;

    default SystemResourceOperator disableAutoUpdate() {
        return this;
    }

    default SystemResourceOperator enableAutoUpdate() {
        return this;
    }

    default boolean isAutoUpdateEnabled() {
        return true;
    }

    SystemResource getSystemResource();

    default <T extends SystemResource> T getSystemResource(Class<T> type) {
        SystemResource systemResource = getSystemResource();
        if (type.isInstance(systemResource)) {
            return type.cast(systemResource);
        }
        throw new IllegalArgumentException("The system resource is not a "
                + type.getName() +
                " instance, it is a "
                + systemResource.getClass().getName()
                + " instance."
        );
    }
}
