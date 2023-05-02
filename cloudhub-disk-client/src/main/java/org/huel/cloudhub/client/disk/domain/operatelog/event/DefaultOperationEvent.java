package org.huel.cloudhub.client.disk.domain.operatelog.event;

import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.OperationEvent;
import org.springframework.context.ApplicationEvent;

/**
 * @author RollW
 */
public abstract class DefaultOperationEvent extends ApplicationEvent
        implements OperationEvent {
    private final Operation operation;

    public DefaultOperationEvent(Operation operation) {
        super(operation);
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }
}
