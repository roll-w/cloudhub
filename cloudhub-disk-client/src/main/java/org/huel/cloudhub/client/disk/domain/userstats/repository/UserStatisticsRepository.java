package org.huel.cloudhub.client.disk.domain.userstats.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserStatisticsDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatistics;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class UserStatisticsRepository extends BaseRepository<UserStatistics> {
    private final UserStatisticsDao userStatisticsDao;

    public UserStatisticsRepository(DiskDatabase database,
                                    CacheManager cacheManager) {
        super(database.getUserStatisticsDao(), cacheManager);
        userStatisticsDao = database.getUserStatisticsDao();
    }

    public UserStatistics getByUserId(long userId, LegalUserType userType) {
        return cacheResult(userStatisticsDao.getByUserId(userId, userType));
    }

    @Override
    protected Class<UserStatistics> getEntityClass() {
        return UserStatistics.class;
    }
}
