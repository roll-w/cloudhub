package org.huel.cloudhub.client.disk.database.dao;

import space.lingu.light.Delete;
import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;
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
}
