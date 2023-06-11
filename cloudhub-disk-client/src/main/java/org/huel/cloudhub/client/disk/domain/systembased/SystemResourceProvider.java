package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface SystemResourceProvider extends SystemResourceSupportable {
    @Override
    boolean supports(SystemResourceKind systemResourceKind);

    /**
     * Provide a system resource by id and kind.
     *
     * @param resourceId         the id of the system resource
     * @param systemResourceKind the kind of the system resource
     * @return the system resource
     * @throws UnsupportedKindException if the system resource kind is not supported.
     */
    SystemResource provide(long resourceId,
                           SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException;
}
