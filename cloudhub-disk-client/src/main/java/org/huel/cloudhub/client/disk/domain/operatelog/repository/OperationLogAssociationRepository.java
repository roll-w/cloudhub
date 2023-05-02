package org.huel.cloudhub.client.disk.domain.operatelog.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.OperationLogAssociationDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogAssociation;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class OperationLogAssociationRepository extends BaseRepository<OperationLogAssociation> {
    private final OperationLogAssociationDao operationLogAssociationDao;

    protected OperationLogAssociationRepository(DiskDatabase database) {
        super(database.getOperationLogAssociationDao());
        operationLogAssociationDao = database.getOperationLogAssociationDao();
    }

    public List<OperationLogAssociation> getByOperationId(long operationId) {
        return operationLogAssociationDao.getByOperationId(operationId);
    }

    public List<OperationLogAssociation> getByResourceId(long resourceId,
                                                         SystemResourceKind resourceKind) {
        return operationLogAssociationDao.getByResourceId(resourceId, resourceKind);
    }
}
