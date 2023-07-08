package org.huel.cloudhub.client.disk.domain.user;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface UserOperator extends SystemResourceOperator,
        SystemResource, AttributedUser {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    UserOperator disableAutoUpdate();

    @Override
    UserOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    UserOperator update() throws BusinessRuntimeException;

    @Override
    UserOperator delete() throws BusinessRuntimeException;

    @Override
    UserOperator rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException;

    UserOperator setNickname(String nickname)
            throws BusinessRuntimeException;

    UserOperator setEmail(String email)
            throws BusinessRuntimeException;

    UserOperator setRole(Role role)
            throws BusinessRuntimeException;

    UserOperator setPassword(String password)
            throws BusinessRuntimeException;

    UserOperator setPassword(String oldPassword, String password)
            throws BusinessRuntimeException;

    UserOperator setEnabled(boolean enabled)
            throws BusinessRuntimeException;

    UserOperator setLocked(boolean locked)
            throws BusinessRuntimeException;

    UserOperator setCanceled(boolean canceled)
            throws BusinessRuntimeException;

    @Override
    UserOperator getSystemResource();


}
