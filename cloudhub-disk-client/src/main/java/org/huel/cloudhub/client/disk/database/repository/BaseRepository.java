package org.huel.cloudhub.client.disk.database.repository;

import org.huel.cloudhub.client.disk.database.dao.AutoPrimaryBaseDao;

import java.util.List;

/**
 * @author RollW
 */
public abstract class BaseRepository<T> {
    protected final AutoPrimaryBaseDao<T> primaryBaseDao;

    protected BaseRepository(AutoPrimaryBaseDao<T> primaryBaseDao) {
        this.primaryBaseDao = primaryBaseDao;
    }

    public long insert(T t) {
        return primaryBaseDao.insertReturns(t);
    }

    public void insert(List<T> ts) {
        primaryBaseDao.insert(ts);
    }

    public void update(T t) {
        primaryBaseDao.update(t);
    }

    public void update(List<T> ts) {
        primaryBaseDao.update(ts);
    }
}
