package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface SystemResourceLocator {
    SystemResource locate(long resourceId,
                          SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException;

    SystemResource locate(SystemResource systemResource)
            throws BusinessRuntimeException, UnsupportedKindException;
}
