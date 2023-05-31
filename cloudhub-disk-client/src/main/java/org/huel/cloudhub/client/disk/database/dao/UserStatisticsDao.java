package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstats.UserStatistics;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserStatisticsDao extends AutoPrimaryBaseDao<UserStatistics> {
    @Override
    @Query("SELECT * FROM user_statistics")
    List<UserStatistics> getActives();

    @Override
    @Query("SELECT * FROM user_statistics")
    List<UserStatistics> getInactives();

    @Override
    @Query("SELECT * FROM user_statistics WHERE id = {id}")
    UserStatistics getById(long id);

    @Override
    @Query("SELECT * FROM user_statistics WHERE id IN ({ids})")
    List<UserStatistics> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_statistics")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_statistics")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_statistics ORDER BY id DESC")
    List<UserStatistics> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_statistics")
    int count();

    @Override
    @Query("SELECT * FROM user_statistics ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserStatistics> get(Offset offset);

    @Query("SELECT * FROM user_statistics WHERE user_id = {userId} AND user_type = {userType}")
    UserStatistics getByUserId(long userId, LegalUserType userType);

    @Override
    default String getTableName() {
        return "user_statistics";
    }
}
