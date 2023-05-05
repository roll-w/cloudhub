package org.huel.cloudhub.client.disk.database.repository;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.database.dao.AutoPrimaryBaseDao;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public abstract class BaseRepository<T extends DataItem> {
    protected final AutoPrimaryBaseDao<T> primaryBaseDao;
    protected final Cache cache;

    protected BaseRepository(AutoPrimaryBaseDao<T> primaryBaseDao,
                             CacheManager cacheManager) {
        this.primaryBaseDao = primaryBaseDao;
        this.cache = cacheManager.getCache("TB-" + primaryBaseDao.getTableName());
    }

    protected BaseRepository(AutoPrimaryBaseDao<T> primaryBaseDao) {
        this.primaryBaseDao = primaryBaseDao;
        this.cache = null;
    }

    public long insert(T t) {
        return primaryBaseDao.insertReturns(t);
    }

    public void insert(List<T> ts) {
        primaryBaseDao.insert(ts);
    }

    public void update(T t) {
        primaryBaseDao.update(t);
        cacheResult(t);
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
        T t = getFromCache(id);
        if (t != null) {
            return t;
        }
        T queried = primaryBaseDao.getById(id);
        cacheResult(queried);

        return queried;
    }

    public List<T> getByIds(List<Long> ids) {
        CacheResult<T> ts = searchFromCache(ids);
        if (ts.missedIds().isEmpty()) {
            return ts.ts();
        }
        List<T> missed = primaryBaseDao.getByIds(ts.missedIds());
        cacheResult(missed);

        List<T> result = new ArrayList<>(ts.ts());
        result.addAll(missed);

        return result;
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

    public void invalidateCache() {
        if (cache == null) {
            return;
        }
        cache.clear();
    }

    protected void cacheResult(T t) {
        if (cache == null) {
            return;
        }
        cache.put(t.getId(), t);
    }

    protected void cacheResult(List<T> t) {
        if (cache == null) {
            return;
        }
        for (T t1 : t) {
            cacheResult(t1);
        }
    }

    protected T getFromCache(long id) {
        if (cache == null) {
            return null;
        }
        return (T) cache.get(id);
    }

    protected CacheResult<T> searchFromCache(List<Long> ids) {
        if (cache == null) {
            return new CacheResult<>(List.of(), ids);
        }
        List<T> ts = new ArrayList<>();
        List<Long> missedIds = new ArrayList<>();
        for (Long id : ids) {
            T t = (T) cache.get(id);
            if (t != null) {
                ts.add(t);
            } else {
                missedIds.add(id);
            }
        }
        return new CacheResult<>(ts, missedIds);
    }

    protected record CacheResult<T>(
            List<T> ts,
            List<Long> missedIds
    ) {
    }

}
