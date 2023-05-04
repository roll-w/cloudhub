package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.huel.cloudhub.web.data.page.Offset;
import space.lingu.light.Dao;
import space.lingu.light.Query;

import java.util.List;

/**
 * @author RollW
 */
@Dao
public interface TagGroupDao extends CountableBaseDao<TagGroup> {

    @Query("SELECT * FROM tag_group WHERE id IN {ids}")
    List<TagGroup> getTagGroupsBy(long[] ids);

    @Query("SELECT * FROM tag_group")
    List<TagGroup> getTagGroups();

    @Query("SELECT * FROM tag_group LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<TagGroup> getTagGroups(Offset offset);

    @Query("SELECT * FROM tag_group WHERE id = {id}")
    TagGroup getTagGroupById(long id);

    @Query("SELECT * FROM tag_group WHERE name = {name}")
    TagGroup getTagGroupByName(String name);

    @Query("SELECT * FROM tag_group WHERE name LIKE {name}")
    List<TagGroup> getTagGroupsByName(String name);

    @Override
    default String getTableName() {
        return "tag_group";
    }
}
