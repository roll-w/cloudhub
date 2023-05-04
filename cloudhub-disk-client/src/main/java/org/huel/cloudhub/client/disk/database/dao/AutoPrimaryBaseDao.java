package org.huel.cloudhub.client.disk.database.dao;

import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
public interface AutoPrimaryBaseDao<T> extends BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertReturns(T t);

    @Query("SELECT * FROM {table} WHERE deleted = 0")
    List<T> getActives(String table);

    @Query("SELECT * FROM {table} WHERE deleted = 1")
    List<T> getInactives(String table);

    @Query("SELECT * FROM {table} WHERE id = {id}")
    T getById(String table, long id);

    @Query("SELECT * FROM {table} WHERE id IN {ids}")
    List<T> getByIds(String table, List<Long> ids);

    @Query("SELECT COUNT(*) FROM {table} WHERE deleted = 0")
    int countActive(String table);

    @Query("SELECT COUNT(*) FROM {table} WHERE deleted = 1")
    int countInactive(String table);

    default long insertReturns(T t, String table) {
        return insertReturns(t);
    }

    default List<T> getActives() {
        return getActives(getTableName());
    }

    default List<T> getInactives() {
        return getInactives(getTableName());
    }

    default T getById(long id) {
        return getById(getTableName(), id);
    }

    default List<T> getByIds(List<Long> ids) {
        return getByIds(getTableName(), ids);
    }

    default int countActive() {
        return countActive(getTableName());
    }

    default int countInactive() {
        return countInactive(getTableName());
    }

}
