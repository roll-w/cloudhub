package org.huel.cloudhub.client.disk.domain.operatelog.context;

import org.huel.cloudhub.client.disk.domain.operatelog.OperateType;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class OperationContext {
    private final Operation.Builder operationBuilder;

    public OperationContext() {
        operationBuilder = Operation.builder();
    }

    public OperationContext(Operation.Builder operationBuilder) {
        this.operationBuilder = operationBuilder;
    }

    public OperationContext setOperator(Operator operator) {
        operationBuilder.setOperator(operator);
        return this;
    }

    public OperationContext addSystemResource(SystemResource systemResource) {
        SystemResource exists = operationBuilder.getSystemResource();
        if (exists == null) {
            operationBuilder.setSystemResource(systemResource);
            return this;
        }
        List<SystemResource> associatedResources = operationBuilder.getAssociatedResources();
        if (associatedResources == null) {
            associatedResources = new ArrayList<>();

        }
        associatedResources.add(systemResource);
        operationBuilder.setAssociatedResources(associatedResources);
        return this;
    }

    public OperationContext addSystemResourceOverrides(SystemResource systemResource) {
        operationBuilder.setSystemResource(systemResource);
        return this;
    }

    public OperationContext setOperateType(OperateType operateType) {
        operationBuilder.setOperateType(operateType);
        return this;
    }

    public OperationContext setAddress(String address) {
        operationBuilder.setAddress(address);
        return this;
    }

    public OperationContext setTimestamp(long timestamp) {
        operationBuilder.setTimestamp(timestamp);
        return this;
    }

    public OperationContext setOriginContent(String originContent) {
        operationBuilder.setOriginContent(originContent);
        return this;
    }

    public OperationContext setChangedContent(String changedContent) {
        operationBuilder.setChangedContent(changedContent);
        return this;
    }

    protected OperationContext setAssociatedResources(SystemResource... associatedResources) {
        operationBuilder.setAssociatedResources(List.of(associatedResources));
        return this;
    }

    protected OperationContext setAssociatedResources(List<SystemResource> associatedResources) {
        operationBuilder.setAssociatedResources(associatedResources);
        return this;
    }

    public Operation build() {
        return operationBuilder.build();
    }

}
