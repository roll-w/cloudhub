package org.huel.cloudhub.client.disk.database.dao;

import space.lingu.light.Query;

/**
 * @author RollW
 */
public interface CountableBaseDao<T> extends AutoPrimaryBaseDao<T> {
    @Query("SELECT COUNT(*) FROM {table}")
    int count(String table);

    @Query("SELECT COUNT(*) FROM {table} WHERE deleted = 0")
    int activeCount(String table);

    @Query("SELECT COUNT(*) FROM {table} WHERE deleted = 1")
    int inactiveCount(String table);


    default int activeCount() {
        return activeCount(getTableName());
    }

    default int inactiveCount() {
        return inactiveCount(getTableName());
    }

}
