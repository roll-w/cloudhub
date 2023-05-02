package org.huel.cloudhub.client.disk.domain.operatelog.service;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.OperateLogger;
import org.huel.cloudhub.client.disk.domain.operatelog.OperateType;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogAssociation;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationService;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;
import org.huel.cloudhub.client.disk.domain.operatelog.repository.OperationLogAssociationRepository;
import org.huel.cloudhub.client.disk.domain.operatelog.repository.OperationLogRepository;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class OperateLogServiceImpl implements OperateLogger, OperationService {
    private static final Logger logger = LoggerFactory.getLogger(OperateLogServiceImpl.class);

    private final OperationLogRepository operationLogRepository;
    private final OperationLogAssociationRepository operationLogAssociationRepository;

    public OperateLogServiceImpl(OperationLogRepository operationLogRepository,
                                 OperationLogAssociationRepository operationLogAssociationRepository) {
        this.operationLogRepository = operationLogRepository;
        this.operationLogAssociationRepository = operationLogAssociationRepository;
    }

    @Override
    public void recordOperate(Action action, SystemResource systemResource,
                              OperateType operateType,
                              String originContent, String changedContent,
                              List<SystemResource> associateResources) {
        ApiContextHolder.ApiContext apiContext = ApiContextHolder.getContext();
        Operator operator = apiContext.userInfo();
        String address = apiContext.ip();

        Operation operation = new Operation(
                operator,
                systemResource,
                operateType,
                address,
                System.currentTimeMillis(),
                originContent,
                changedContent,
                associateResources
        );
        recordOperate(operation);
    }

    @Override
    public void recordOperate(Action action, SystemResource systemResource,
                              OperateType operateType,
                              String originContent, List<SystemResource> associateResources) {
        recordOperate(action, systemResource, operateType, originContent, null, associateResources);
    }

    @Override
    public void recordOperate(Action action, SystemResource systemResource,
                              OperateType operateType,
                              List<SystemResource> associateResources) {
        recordOperate(action, systemResource, operateType,
                null, associateResources);
    }

    @Override
    public void recordOperate(Operation operation) {
        recordOperation(operation);
    }

    @Override
    public void recordOperation(Operation operation) {
        OperationLog operationLog = OperationLog.builder()
                .setOperator(operation.operator().getOperatorId())
                .setOperateTime(operation.timestamp())
                .setAction(operation.operateType().getAction())
                .setOperateType(operation.operateType().getTypeId())
                .setOperateResourceId(operation.systemResource().getResourceId())
                .setSystemResourceKind(operation.systemResource().getSystemResourceKind())
                .setOriginContent(operation.originContent())
                .setChangedContent(operation.changedContent())
                .setAddress(operation.address())
                .build();
        logger.debug("Log operation: {}", operationLog);
        long id = operationLogRepository.insert(operationLog);
        buildAssociation(operation, id);
    }

    private void buildAssociation(Operation operation, long id) {
        List<SystemResource> associateResources = operation.associatedResources();
        if (associateResources == null || associateResources.isEmpty()) {
            return;
        }
        List<OperationLogAssociation> associations = new ArrayList<>();
        for (SystemResource associateResource : associateResources) {
            OperationLogAssociation association = OperationLogAssociation.builder()
                    .setOperationId(id)
                    .setResourceId(associateResource.getResourceId())
                    .setResourceKind(associateResource.getSystemResourceKind())
                    .build();
            associations.add(association);
        }
        operationLogAssociationRepository.insert(associations);
    }
}
