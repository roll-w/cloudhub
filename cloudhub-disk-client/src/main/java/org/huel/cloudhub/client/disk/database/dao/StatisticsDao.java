package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.statistics.Statistics;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface StatisticsDao extends AutoPrimaryBaseDao<Statistics> {
    @Override
    @Query("SELECT * FROM statistics WHERE deleted = 0")
    List<Statistics> getActives();

    @Override
    @Query("SELECT * FROM statistics WHERE deleted = 1")
    List<Statistics> getInactives();

    @Override
    @Query("SELECT * FROM statistics WHERE id = {id}")
    Statistics getById(long id);

    @Override
    @Query("SELECT * FROM statistics WHERE id IN ({ids})")
    List<Statistics> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM statistics WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM statistics WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM statistics ORDER BY id DESC")
    List<Statistics> get();

    @Override
    @Query("SELECT COUNT(*) FROM statistics")
    int count();

    @Override
    @Query("SELECT * FROM statistics ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<Statistics> get(Offset offset);

    @Override
    default String getTableName() {
        return "statistics";
    }

    @Query("SELECT * FROM statistics WHERE statistics_key = {statisticsKey}")
    Statistics getByKey(String statisticsKey);
}
