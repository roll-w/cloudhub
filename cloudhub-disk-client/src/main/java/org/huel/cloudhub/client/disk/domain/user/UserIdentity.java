package org.huel.cloudhub.client.disk.domain.user;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public interface UserIdentity extends Operator, StorageOwner {
    long getUserId();

    @Override
    default long getOperatorId() {
        return getUserId();
    }

    String getUsername();

    String getEmail();

    Role getRole();
}
