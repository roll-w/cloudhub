package org.huel.cloudhub.client.disk.domain.statistics.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.DatedStatisticsDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.statistics.DatedStatistics;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class DatedStatisticsRepository extends BaseRepository<DatedStatistics> {
    private final DatedStatisticsDao datedStatisticsDao;

    public DatedStatisticsRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getDatedStatisticsDao(), pageableContextThreadAware, cacheManager);
        datedStatisticsDao = database.getDatedStatisticsDao();
    }

    @Override
    protected Class<DatedStatistics> getEntityClass() {
        return DatedStatistics.class;
    }
}
