package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;

/**
 * @author RollW
 */
public interface SystemAuthentication {
    SystemResource getSystemResource();

    Operator getOperator();

    boolean isAuthenticated();

    boolean isAllowAccess();

    /**
     * Throws an {@link AuthenticationException} if the user is not authenticated.
     */
    void throwAuthenticationException() throws AuthenticationException;
}
