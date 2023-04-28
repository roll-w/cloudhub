package org.huel.cloudhub.client.disk.domain.operatelog;

/**
 * An operator is a user who performs an operation.
 * Can only be a {@link org.huel.cloudhub.client.disk.domain.user.LegalUserType#USER}.
 *
 * @author RollW
 */
public interface Operator {
    long getOperatorId();
}
