package org.huel.cloudhub.client.disk.database.repository;

import org.huel.cloudhub.client.disk.database.dao.AutoPrimaryBaseDao;
import org.huel.cloudhub.web.data.page.Offset;

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

    public List<T> getActives() {
        return primaryBaseDao.getActives();
    }

    public List<T> getInactives() {
        return primaryBaseDao.getInactives();
    }

    public T getById(long id) {
        return primaryBaseDao.getById(id);
    }

    public List<T> getByIds(List<Long> ids) {
        return primaryBaseDao.getByIds(ids);
    }

    public int countActive() {
        return primaryBaseDao.countActive();
    }

    public int countInactive() {
        return primaryBaseDao.countInactive();
    }

    public int count() {
        return primaryBaseDao.count();
    }

    public List<T> get() {
        return primaryBaseDao.get();
    }

    public List<T> get(Offset offset) {
        return primaryBaseDao.get(offset);
    }
}
