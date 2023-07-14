package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.statistics.DatedStatistics;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface DatedStatisticsDao extends AutoPrimaryBaseDao<DatedStatistics> {
    @Override
    @Query("SELECT * FROM dated_statistics WHERE deleted = 0")
    List<DatedStatistics> getActives();

    @Override
    @Query("SELECT * FROM dated_statistics WHERE deleted = 1")
    List<DatedStatistics> getInactives();

    @Override
    @Query("SELECT * FROM dated_statistics WHERE id = {id}")
    DatedStatistics getById(long id);

    @Override
    @Query("SELECT * FROM dated_statistics WHERE id IN ({ids})")
    List<DatedStatistics> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM dated_statistics WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM dated_statistics WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM dated_statistics ORDER BY id DESC")
    List<DatedStatistics> get();

    @Override
    @Query("SELECT COUNT(*) FROM dated_statistics")
    int count();

    @Override
    @Query("SELECT * FROM dated_statistics ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<DatedStatistics> get(Offset offset);

    @Override
    default String getTableName() {
        return "dated_statistics";
    }

    @Query("SELECT * FROM dated_statistics WHERE `key` = {statisticsKey} ORDER BY id DESC LIMIT 1")
    DatedStatistics getLatestOfKey(String statisticsKey);

    @Query("SELECT * FROM dated_statistics WHERE `key` = {statisticsKey} AND `date` = {date}")
    DatedStatistics getByKeyAndDate(String statisticsKey,
                                    LocalDate date);
}

