package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.usergroup.UserGroup;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface UserGroupDao extends AutoPrimaryBaseDao<UserGroup> {
    @Override
    @Query("SELECT * FROM user_group WHERE deleted = 0")
    List<UserGroup> getActives();

    @Override
    @Query("SELECT * FROM user_group WHERE deleted = 1")
    List<UserGroup> getInactives();

    @Override
    @Query("SELECT * FROM user_group WHERE id = {id}")
    UserGroup getById(long id);

    @Override
    @Query("SELECT * FROM user_group WHERE id IN ({ids})")
    List<UserGroup> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM user_group WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM user_group WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM user_group")
    List<UserGroup> get();

    @Override
    @Query("SELECT COUNT(*) FROM user_group")
    int count();

    @Override
    @Query("SELECT * FROM user_group LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<UserGroup> get(Offset offset);

    @Override
    default String getTableName() {
        return "user_group";
    }

    @Query("SELECT * FROM user_group WHERE name = {name} LIMIT 1")
    UserGroup getByName(String name);
}
