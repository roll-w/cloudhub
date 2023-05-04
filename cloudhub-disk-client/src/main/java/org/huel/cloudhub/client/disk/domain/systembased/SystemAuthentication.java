package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;

/**
 * @author RollW
 */
public interface SystemAuthentication {
    boolean isAuthenticated();

    boolean isAllowAccess();

    void throwAuthenticationException() throws AuthenticationException;
}
