package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface FavoriteItemDao extends AutoPrimaryBaseDao<FavoriteItem> {
    @Override
    @Query("SELECT * FROM favorite_item WHERE deleted = 0")
    List<FavoriteItem> getActives();

    @Override
    @Query("SELECT * FROM favorite_item WHERE deleted = 1")
    List<FavoriteItem> getInactives();

    @Override
    @Query("SELECT * FROM favorite_item WHERE id = {id}")
    FavoriteItem getById(long id);

    @Override
    @Query("SELECT * FROM favorite_item WHERE id IN ({ids})")
    List<FavoriteItem> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM favorite_item WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_item WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM favorite_item ORDER BY id DESC")
    List<FavoriteItem> get();

    @Override
    @Query("SELECT COUNT(*) FROM favorite_item")
    int count();

    @Override
    @Query("SELECT * FROM favorite_item ORDER BY id DESC LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<FavoriteItem> get(Offset offset);

    @Override
    default String getTableName() {
        return "favorite_item";
    }

}

