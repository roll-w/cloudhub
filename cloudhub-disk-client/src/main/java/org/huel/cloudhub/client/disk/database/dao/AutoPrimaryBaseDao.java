package org.huel.cloudhub.client.disk.database.dao;

import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;

/**
 * @author RollW
 */
public interface AutoPrimaryBaseDao<T> extends BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertReturns(T t);
}
