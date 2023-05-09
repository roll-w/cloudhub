package org.huel.cloudhub.client.disk.system.pages;

import org.huel.cloudhub.client.disk.common.CacheNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/**
 * @author RollW
 */
@Component
public class PagesCache {
    private static final Logger logger = LoggerFactory.getLogger(PagesCache.class);

    private final Cache cache;
    private final List<CountableDao<?>> countableDaos;

    public PagesCache(CacheManager cacheManager,
                      List<CountableDao<?>> countableDaos) {
        this.cache = cacheManager.getCache(CacheNames.PAGES);
        this.countableDaos = countableDaos;
    }

    public long getCount(Class<?> type) {
        String key = toCountKey(type);
        CountableDao<?> countableDao = findCountableDao(type);
        if (countableDao == null) {
            logger.error("No CountableDao found for type: {}", type.getName());
            return 0;
        }
        return tryGetCount(key, countableDao::getCount);
    }

    public long getActiveCount(Class<?> type) {
        String key = toActiveKey(type);
        CountableDao<?> countableDao = findCountableDao(type);
        if (countableDao == null) {
            logger.error("No CountableDao found for type: {}", type.getName());
            return 0;
        }
        return tryGetCount(key, countableDao::getActiveCount);
    }

    // allow to set count manually

    public void setCount(Class<?> type, long count) {
        String key = toCountKey(type);
        trySetCount(key, count);
    }

    public void setActiveCount(Class<?> type, long count) {
        String key = toActiveKey(type);
        trySetCount(key, count);
    }

    private long tryGetCount(String key,
                             LongSupplier longProvider) {
        AtomicLong atomicLong = cache.get(key, AtomicLong.class);
        if (atomicLong != null) {
            return atomicLong.get();
        }
        long value = longProvider.getAsLong();
        cache.put(key, new AtomicLong(value));
        return value;
    }

    private void trySetCount(String key, long value) {
        AtomicLong atomicLong = cache.get(key, AtomicLong.class);
        if (atomicLong != null) {
            atomicLong.set(value);
            return;
        }
        cache.put(key, new AtomicLong(value));
    }

    private CountableDao<?> findCountableDao(Class<?> type) {
        for (CountableDao<?> countableDao : countableDaos) {
            if (countableDao.getCountableType().equals(type)) {
                return countableDao;
            }
        }
        return null;
    }

    private String toActiveKey(Class<?> clz) {
        return clz.getCanonicalName() + ".active";
    }

    private String toCountKey(Class<?> clz) {
        return clz.getCanonicalName() + ".normal";
    }
}
