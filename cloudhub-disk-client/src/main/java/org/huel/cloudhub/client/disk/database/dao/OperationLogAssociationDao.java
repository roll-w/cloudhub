package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.operatelog.OperationLogAssociation;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
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
    default String getTableName() {
        return "operation_log_association";
    }
}
