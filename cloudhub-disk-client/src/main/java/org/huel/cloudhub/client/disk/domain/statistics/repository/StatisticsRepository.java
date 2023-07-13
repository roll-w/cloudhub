package org.huel.cloudhub.client.disk.domain.statistics.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.StatisticsDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.statistics.Statistics;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class StatisticsRepository extends BaseRepository<Statistics> {
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
        return cacheResult(statisticsDao.getByKey(statisticsKey));
    }
}
