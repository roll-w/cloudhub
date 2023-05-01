package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.operatelog.OperationLog;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface OperationLogDao extends AutoPrimaryBaseDao<OperationLog> {
    @Query("SELECT * FROM operation_log WHERE operate_resource_id = {resourceId} AND resource_kind = {resourceKind}")
    List<OperationLog> getOperationLogsByResourceId(long resourceId, SystemResourceKind resourceKind);

    @Query("SELECT * FROM operation_log WHERE operator = {operator}")
    List<OperationLog> getByOperator(long operator);
}
