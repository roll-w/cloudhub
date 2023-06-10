package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.web.AuthErrorCode;

/**
 * @author RollW
 */
public class SimpleSystemAuthentication implements SystemAuthentication {
    private final SystemResource systemResource;
    private final Operator operator;
    private final boolean allow;

    public SimpleSystemAuthentication(SystemResource systemResource,
                                      Operator operator,
                                      boolean allow) {
        this.systemResource = systemResource;
        this.operator = operator;
        this.allow = allow;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public boolean isAllowAccess() {
        return allow;
    }

    @Override
    public SystemResource getSystemResource() {
        return systemResource;
    }

    public Operator getOperator() {
        return operator;
    }

    public boolean isAllow() {
        return allow;
    }

    @Override
    public void throwAuthenticationException() throws AuthenticationException {
        if (!isAuthenticated()) {
            throw new AuthenticationException(AuthErrorCode.ERROR_UNKNOWN_AUTH,
                    "Cannot authenticate the current user with given resource: " + systemResource.getSystemResourceKind());
        }

        if (!isAllowAccess()) {
            throw new AuthenticationException(AuthErrorCode.ERROR_NOT_HAS_ROLE,
                    "You have no permission to access this resource.");
        }
    }
}
