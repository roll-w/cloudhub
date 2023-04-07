package org.huel.cloudhub.client.disk.domain.user;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;

/**
 * @author RollW
 */
public interface UserIdentity extends Operator {
    long getUserId();

    @Override
    default long getOperatorId() {
        return getUserId();
    }

    String getUsername();

    String getEmail();

    Role getRole();
}
