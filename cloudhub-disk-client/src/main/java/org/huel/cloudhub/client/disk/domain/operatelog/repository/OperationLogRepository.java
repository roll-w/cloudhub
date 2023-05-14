package org.huel.cloudhub.client.disk.domain.operatelog.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.OperationLogDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class OperationLogRepository extends BaseRepository<OperationLog> {
    private final OperationLogDao operationLogDao;

    public OperationLogRepository(DiskDatabase database,
                                  CacheManager cacheManager) {
        super(database.getOperationLogDao(), cacheManager);
        operationLogDao = database.getOperationLogDao();
    }

    public List<OperationLog> getOperationLogsByResourceId(long resourceId,
                                                           SystemResourceKind resourceKind) {
        List<OperationLog> operationLogs =
                operationLogDao.getOperationLogsByResourceId(resourceId, resourceKind);
        return cacheResult(operationLogs);
    }

    public List<OperationLog> getByOperator(long operator) {
        List<OperationLog> operationLogs =
                operationLogDao.getByOperator(operator);
        return cacheResult(operationLogs);
    }

    public List<OperationLog> getByOperator(long operator, Pageable pageable) {
        return cacheResult(
                operationLogDao.getByOperator(operator, pageable.toOffset())
        );
    }

    public List<OperationLog> getOperationLogsByResourceId(long resourceId,
                                                           SystemResourceKind resourceKind,
                                                           Pageable pageable) {
        return cacheResult(
                operationLogDao.getOperationLogsByResourceId(resourceId, resourceKind, pageable.toOffset())
        );
    }

    @Override
    protected Class<OperationLog> getEntityClass() {
        return OperationLog.class;
    }

    public long countByOperator(long operatorId) {
        return operationLogDao.count(operatorId);
    }

    public long countByResourceId(long resourceId, SystemResourceKind resourceKind) {
        return operationLogDao.count(resourceId, resourceKind);
    }
}
