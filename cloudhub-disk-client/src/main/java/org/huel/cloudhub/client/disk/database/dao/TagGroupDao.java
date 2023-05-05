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
public interface TagGroupDao extends AutoPrimaryBaseDao<TagGroup> {

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
    @Query("SELECT * FROM tag_group WHERE deleted = 0")
    List<TagGroup> getActives();

    @Override
    @Query("SELECT * FROM tag_group WHERE deleted = 1")
    List<TagGroup> getInactives();

    @Override
    @Query("SELECT * FROM tag_group WHERE id = {id}")
    TagGroup getById(long id);

    @Override
    @Query("SELECT * FROM tag_group WHERE id IN {ids}")
    List<TagGroup> getByIds(List<Long> ids);

    @Override
    @Query("SELECT COUNT(*) FROM tag_group WHERE deleted = 0")
    int countActive();

    @Override
    @Query("SELECT COUNT(*) FROM tag_group WHERE deleted = 1")
    int countInactive();

    @Override
    @Query("SELECT * FROM tag_group")
    List<TagGroup> get();

    @Override
    @Query("SELECT COUNT(*) FROM tag_group")
    int count();

    @Override
    @Query("SELECT * FROM tag_group LIMIT {offset.limit()} OFFSET {offset.offset()}")
    List<TagGroup> get(Offset offset);

    @Override
    default String getTableName() {
        return "tag_group";
    }
}
