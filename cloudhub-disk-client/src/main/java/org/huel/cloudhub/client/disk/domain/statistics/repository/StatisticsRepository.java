package org.huel.cloudhub.client.disk.domain.statistics.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.StatisticsDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.statistics.Statistics;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author RollW
 */
@Repository
public class StatisticsRepository extends BaseRepository<Statistics> {
    private static final Statistics DUMMY =
            new Statistics(-1L, null, Map.of());

    private final StatisticsDao statisticsDao;

    protected StatisticsRepository(DiskDatabase database,
                                   ContextThreadAware<PageableContext> pageableContextThreadAware,
                                   CacheManager cacheManager) {
        super(database.getStatisticsDao(), pageableContextThreadAware, cacheManager);
        statisticsDao = database.getStatisticsDao();
    }

    @Override
    protected Class<Statistics> getEntityClass() {
        return Statistics.class;
    }

    public Statistics getByKey(String statisticsKey) {
        if (statisticsKey == null) {
            return null;
        }
        Statistics cached = cache.get(statisticsKey, Statistics.class);
        if (cached == DUMMY) {
            return null;
        }
        if (cached != null) {
            return cached;
        }
        Statistics statistics = statisticsDao.getByKey(statisticsKey);
        if (statistics == null) {
            cache.put(statisticsKey, DUMMY);
            return null;
        }
        return cacheResult(statistics);
    }

    @Override
    protected Statistics cacheResult(Statistics statistics) {
        if (statistics == null) {
            return null;
        }
        cache.put(statistics.getKey(), statistics);
        return super.cacheResult(statistics);
    }

    @Override
    protected void invalidateCache(Statistics statistics) {
        super.invalidateCache(statistics);
        if (statistics == null || statistics.getKey() == null) {
            return;
        }
        cache.evict(statistics.getKey());
    }
}
