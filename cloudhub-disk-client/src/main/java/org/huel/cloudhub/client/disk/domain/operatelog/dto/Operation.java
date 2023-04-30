package org.huel.cloudhub.client.disk.domain.operatelog.dto;

import org.huel.cloudhub.client.disk.domain.operatelog.OperateType;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.SimpleOperator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;

import java.util.List;

/**
 * @author RollW
 */
public record Operation(
        Operator operator,
        SystemResource systemResource,
        OperateType operateType,
        String address,
        long timestamp,
        String originContent,
        String changedContent,
        List<SystemResource> associatedResources
) {

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Operator operator;
        private SystemResource systemResource;
        private OperateType operateType;
        private String address;
        private long timestamp;
        private String originContent;
        private String changedContent;
        private List<SystemResource> associatedResources;

        public Builder() {
        }

        public Builder(Operation operation) {
            this.operator = operation.operator;
            this.systemResource = operation.systemResource;
            this.operateType = operation.operateType;
            this.address = operation.address;
            this.timestamp = operation.timestamp;
            this.originContent = operation.originContent;
            this.changedContent = operation.changedContent;
            this.associatedResources = operation.associatedResources;
        }

        public Builder setOperator(Operator operator) {
            this.operator = operator;
            return this;
        }

        public Builder setOperator(long operatorId) {
            this.operator = new SimpleOperator(operatorId);
            return this;
        }

        public Builder setSystemResource(SystemResource systemResource) {
            this.systemResource = systemResource;
            return this;
        }

        public Builder setOperateType(OperateType operateType) {
            this.operateType = operateType;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setOriginContent(String originContent) {
            this.originContent = originContent;
            return this;
        }

        public Builder setChangedContent(String changedContent) {
            this.changedContent = changedContent;
            return this;
        }

        public Builder setAssociatedResources(List<SystemResource> associatedResources) {
            this.associatedResources = associatedResources;
            return this;
        }

        public Operator getOperator() {
            return operator;
        }

        public SystemResource getSystemResource() {
            return systemResource;
        }

        public OperateType getOperateType() {
            return operateType;
        }

        public String getAddress() {
            return address;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getOriginContent() {
            return originContent;
        }

        public String getChangedContent() {
            return changedContent;
        }

        public List<SystemResource> getAssociatedResources() {
            return associatedResources;
        }

        public Operation build() {
            return new Operation(
                    operator, systemResource, operateType,
                    address, timestamp, originContent,
                    changedContent, associatedResources);
        }
    }
}
