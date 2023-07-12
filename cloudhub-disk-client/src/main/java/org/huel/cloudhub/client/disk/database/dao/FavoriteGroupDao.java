package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteGroup;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface FavoriteGroupDao extends AutoPrimaryBaseDao<FavoriteGroup> {
    @Override
    @Query("SELECT * FROM favorite_group WHERE deleted = 0")
    List<FavoriteGroup> getActives();

    @Override
    @Query("SELECT * FROM favorite_group WHERE deleted = 1")
    List<FavoriteGroup> getInactives();

    @Override
    @Query("SELECT * FROM favorite_group WHERE id = {id}")
    FavoriteGroup getById(long id);

    @Override
    @Query("SELECT * FROM favorite_group WHERE id IN ({ids})")
    List<FavoriteGroup> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM favorite_group WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_group WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM favorite_group ORDER BY id DESC")
    List<FavoriteGroup> get();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_group")
    int count();

    @Override
    @Query("SELECT * FROM favorite_group ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<FavoriteGroup> get(Offset offset);

    @Override
    default String getTableName() {
        return "favorite_group";
    }

    @Query("SELECT * FROM favorite_group WHERE name = {name} AND user_id = {operator.getOperatorId()} ")
    FavoriteGroup getByName(String name, Operator operator);
}
