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

    @Query("SELECT * FROM content_tag WHERE id IN {ids}")
    List<ContentTag> getTagsBy(long[] ids);

    @Query("SELECT * FROM content_tag")
    List<ContentTag> getTags();

    @Query("SELECT * FROM content_tag LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<ContentTag> getTags(Offset offset);

    @Query("SELECT * FROM content_tag WHERE id = {id}")
    ContentTag getTagById(long id);

    @Override
    default String getTableName() {
        return "content_tag";
    }
}
