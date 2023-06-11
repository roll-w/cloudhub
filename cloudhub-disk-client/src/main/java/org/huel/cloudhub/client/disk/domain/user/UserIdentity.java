package org.huel.cloudhub.client.disk.domain.user;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public interface UserIdentity extends Operator, StorageOwner, SystemResource {
    long getUserId();

    @Override
    default long getOperatorId() {
        return getUserId();
    }

    String getUsername();

    String getEmail();

    Role getRole();

    @Override
    default long getResourceId() {
        return getUserId();
    }

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.USER;
    }

    @Override
    default long getOwnerId() {
        return getUserId();
    }

    @Override
    default LegalUserType getOwnerType() {
        return LegalUserType.USER;
    }
}
