package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface ContentTagDao extends AutoPrimaryBaseDao<ContentTag> {

    @Query("SELECT * FROM content_tag")
    List<ContentTag> getTags();

    @Query("SELECT * FROM content_tag LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<ContentTag> getTags(Offset offset);

    @Query("SELECT * FROM content_tag WHERE id = {id}")
    ContentTag getTagById(long id);

    @Override
    @Query("SELECT * FROM content_tag WHERE deleted = 0")
    List<ContentTag> getActives();

    @Override
    @Query("SELECT * FROM content_tag WHERE deleted = 1")
    List<ContentTag> getInactives();

    @Override
    @Query("SELECT * FROM content_tag WHERE id = {id}")
    ContentTag getById(long id);

    @Override
    @Query("SELECT * FROM content_tag WHERE id IN ({ids})")
    List<ContentTag> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM content_tag WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM content_tag WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM content_tag")
    List<ContentTag> get();

    @Override
    @Query("SELECT COUNT(*) FROM content_tag")
    int count();

    @Override
    @Query("SELECT * FROM content_tag LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<ContentTag> get(Offset offset);

    @Override
    default String getTableName() {
        return "content_tag";
    }

    @Query("SELECT * FROM content_tag WHERE name = {name}")
    ContentTag getByName(String name);

    @Query("SELECT * FROM content_tag WHERE name IN ({names})")
    List<ContentTag> getByNames(List<String> names);
}
