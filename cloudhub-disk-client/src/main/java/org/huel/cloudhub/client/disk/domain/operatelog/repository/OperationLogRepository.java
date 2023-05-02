package org.huel.cloudhub.client.disk.domain.operatelog.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.OperationLogDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class OperationLogRepository extends BaseRepository<OperationLog> {
    private final OperationLogDao operationLogDao;

    public OperationLogRepository(DiskDatabase database) {
        super(database.getOperationLogDao());
        operationLogDao = database.getOperationLogDao();
    }

    public List<OperationLog> getOperationLogsByResourceId(long resourceId,
                                                           SystemResourceKind resourceKind) {
        return operationLogDao.getOperationLogsByResourceId(resourceId, resourceKind);
    }

    public List<OperationLog> getByOperator(long operator) {
        return operationLogDao.getByOperator(operator);
    }
}
