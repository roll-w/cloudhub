package org.huel.cloudhub.client.disk.domain.tag.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.TagGroupDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

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

    public TagGroup getTagGroupByName(String name) {
        return cacheResult(tagGroupDao.getTagGroupByName(name));
    }

    @Override
    protected Class<TagGroup> getEntityClass() {
        return TagGroup.class;
    }
}
