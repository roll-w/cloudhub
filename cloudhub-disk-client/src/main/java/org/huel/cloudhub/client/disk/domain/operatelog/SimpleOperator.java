package org.huel.cloudhub.client.disk.domain.operatelog;

/**
 * @author RollW
 */
public record SimpleOperator(long operatorId) implements Operator {
    @Override
    public long getOperatorId() {
        return operatorId;
    }
}
