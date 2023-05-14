package org.huel.cloudhub.client.disk.domain.operatelog.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.OperationLogAssociationDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogAssociation;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class OperationLogAssociationRepository extends BaseRepository<OperationLogAssociation> {
    private final OperationLogAssociationDao operationLogAssociationDao;

    protected OperationLogAssociationRepository(DiskDatabase database,
                                                CacheManager cacheManager) {
        super(database.getOperationLogAssociationDao(), cacheManager);
        operationLogAssociationDao = database.getOperationLogAssociationDao();
    }

    public List<OperationLogAssociation> getByOperationId(long operationId) {
        List<OperationLogAssociation> operationLogAssociations =
                operationLogAssociationDao.getByOperationId(operationId);
        return cacheResult(operationLogAssociations);
    }

    public List<OperationLogAssociation> getByOperationIds(List<Long> operationIds) {
        if (operationIds == null || operationIds.isEmpty()) {
            return List.of();
        }
        return cacheResult(
                operationLogAssociationDao.getByOperationIds(operationIds)
        );
    }

    public List<OperationLogAssociation> getByResourceId(long resourceId,
                                                         SystemResourceKind resourceKind) {
        List<OperationLogAssociation> operationLogAssociations =
                operationLogAssociationDao.getByResourceId(resourceId, resourceKind);
        return cacheResult(operationLogAssociations);
    }

    @Override
    protected Class<OperationLogAssociation> getEntityClass() {
        return OperationLogAssociation.class;
    }
}
