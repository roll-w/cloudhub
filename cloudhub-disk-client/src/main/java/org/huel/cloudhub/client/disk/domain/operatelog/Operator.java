package org.huel.cloudhub.client.disk.domain.operatelog;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * An operator is a user who performs an operation.
 * Can only be a {@link org.huel.cloudhub.client.disk.domain.user.LegalUserType#USER}.
 *
 * @author RollW
 */
public interface Operator extends StorageOwner {
    long getOperatorId();

    @Override
    default long getOwnerId() {
        return getOperatorId();
    }

    @Override
    default LegalUserType getOwnerType() {
        return LegalUserType.USER;
    }
}
