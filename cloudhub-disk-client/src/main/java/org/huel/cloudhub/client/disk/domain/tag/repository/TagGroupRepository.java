package org.huel.cloudhub.client.disk.domain.tag.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.TagGroupDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class TagGroupRepository extends BaseRepository<TagGroup> {
    private final TagGroupDao tagGroupDao;

    public TagGroupRepository(DiskDatabase database,
                              CacheManager cacheManager) {
        super(database.getTagGroupDao(), cacheManager);
        tagGroupDao = database.getTagGroupDao();
    }

    public List<TagGroup> getTagGroupsBy(long[] ids) {
        return tagGroupDao.getTagGroupsBy(ids);
    }

    public List<TagGroup> getTagGroups(Offset offset) {
        return tagGroupDao.getTagGroups(offset);
    }

    public List<TagGroup> getTagGroups() {
        return tagGroupDao.getTagGroups();
    }

    public TagGroup getTagGroupById(long id) {
        return tagGroupDao.getTagGroupById(id);
    }

    public TagGroup getTagGroupByName(String name) {
        return tagGroupDao.getTagGroupByName(name);
    }

    @Override
    protected Class<TagGroup> getEntityClass() {
        return TagGroup.class;
    }
}
