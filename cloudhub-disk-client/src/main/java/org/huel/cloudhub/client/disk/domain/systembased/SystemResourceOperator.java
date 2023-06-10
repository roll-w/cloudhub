package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface SystemResourceOperator extends ByStatusProvider {
    SystemResource update() throws BusinessRuntimeException;

    SystemResource delete() throws BusinessRuntimeException;

    SystemResource rename(String newName) throws BusinessRuntimeException,
            UnsupportedOperationException;

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
