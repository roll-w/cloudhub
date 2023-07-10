package org.huel.cloudhub.client.disk.domain.usergroup;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.web.BusinessRuntimeException;
import space.lingu.NonNull;

import java.util.Map;

/**
 * @author RollW
 */
public interface UserGroupOperator extends SystemResource, SystemResourceOperator {

    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.USER_GROUP;
    }

    @Override
    UserGroupOperator update() throws BusinessRuntimeException;

    @Override
    UserGroupOperator delete() throws BusinessRuntimeException;

    @Override
    UserGroupOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException;

    @Override
    UserGroupOperator disableAutoUpdate();

    @Override
    UserGroupOperator enableAutoUpdate();

    @Override
    boolean isAutoUpdateEnabled();

    @Override
    default SystemResource getSystemResource() {
        return this;
    }

    UserGroupOperator setName(String name);

    UserGroupOperator setDescription(String description);

    UserGroupOperator setSettings(Map<String, String> settings);

    UserGroupOperator setSetting(String key, String value);

    UserGroupOperator addMember(@NonNull StorageOwner storageOwner);

    UserGroupOperator removeMember(@NonNull StorageOwner storageOwner);

    UserGroup getUserGroup();
}
