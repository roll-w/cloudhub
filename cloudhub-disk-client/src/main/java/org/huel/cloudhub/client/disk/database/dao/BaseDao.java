package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Delete;
import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;
import space.lingu.light.Query;
import space.lingu.light.Update;

import java.util.List;

/**
 * @author RollW
 */
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(T t);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(List<T> ts);

    @Update(onConflict = OnConflictStrategy.ABORT)
    void update(T t);

    @Update(onConflict = OnConflictStrategy.ABORT)
    void update(List<T> ts);

    @Delete
    void delete(T t);

    @Delete
    void delete(List<T> ts);

    @Query("SELECT * FROM {table}")
    List<T> get(String table);

    @Query("SELECT COUNT(*) FROM {table}")
    int count(String table);

    @Query("SELECT * FROM {table} LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<T> get(String table, Offset offset);

    default List<T> get() {
        return get(getTableName());
    }

    default int count() {
        return count(getTableName());
    }

    default List<T> get(Offset offset) {
        return get(getTableName(), offset);
    }

    default String getTableName() {
        return null;
    }
}
