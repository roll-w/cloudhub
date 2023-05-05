package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Insert;
import space.lingu.light.OnConflictStrategy;

import java.util.List;

/**
 * @author RollW
 */
public interface AutoPrimaryBaseDao<T extends DataItem> extends BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertReturns(T t);

    default List<T> getActives() {
        return List.of();
    }

    default List<T> getInactives() {
        return List.of();
    }

    default T getById(long id) {
        return null;
    }

    default List<T> getByIds(List<Long> ids) {
        return List.of();
    }

    default int countActive() {
        return 0;
    }

    default int countInactive() {
        return 0;
    }

    @Override
    default List<T> get() {
        return BaseDao.super.get();
    }

    @Override
    default int count() {
        return BaseDao.super.count();
    }

    @Override
    default List<T> get(Offset offset) {
        return BaseDao.super.get(offset);
    }

    @Override
    default String getTableName() {
        return BaseDao.super.getTableName();
    }
}
