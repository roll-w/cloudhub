package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogAssociation;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface OperationLogAssociationDao extends AutoPrimaryBaseDao<OperationLogAssociation> {

    @Query("SELECT * FROM operation_log_association WHERE operation_id = {operationId}")
    List<OperationLogAssociation> getByOperationId(long operationId);

    @Query("SELECT * FROM operation_log_association WHERE resource_id = {resourceId} AND resource_kind = {resourceKind}")
    List<OperationLogAssociation> getByResourceId(long resourceId, SystemResourceKind resourceKind);

    @Override
    @Query("SELECT * FROM operation_log_association WHERE deleted = 0")
    List<OperationLogAssociation> getActives();

    @Override
    @Query("SELECT * FROM operation_log_association WHERE deleted = 1")
    List<OperationLogAssociation> getInactives();

    @Override
    @Query("SELECT * FROM operation_log_association WHERE id = {id}")
    OperationLogAssociation getById(long id);

    @Override
    @Query("SELECT * FROM operation_log_association WHERE id IN ({ids})")
    List<OperationLogAssociation> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM operation_log_association WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM operation_log_association WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM operation_log_association")
    List<OperationLogAssociation> get();

    @Override
    @Query("SELECT COUNT(*) FROM operation_log_association")
    int count();

    @Override
    @Query("SELECT * FROM operation_log_association LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<OperationLogAssociation> get(Offset offset);

    @Override
    default String getTableName() {
        return "operation_log_association";
    }

    @Query("SELECT * FROM operation_log_association WHERE operation_id IN ({operationIds})")
    List<OperationLogAssociation> getByOperationIds(List<Long> operationIds);
}
