package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserShareDao extends AutoPrimaryBaseDao<UserShare> {

    @Override
    @Query("SELECT * FROM user_share WHERE deleted = 0")
    List<UserShare> getActives();

    @Override
    @Query("SELECT * FROM user_share WHERE deleted = 1")
    List<UserShare> getInactives();

    @Override
    @Query("SELECT * FROM user_share WHERE id = {id}")
    UserShare getById(long id);

    @Override
    @Query("SELECT * FROM user_share WHERE id IN {ids}")
    List<UserShare> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_share WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_share WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_share")
    List<UserShare> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_share")
    int count();

    @Override
    @Query("SELECT * FROM user_share LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserShare> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_share";
    }
}
