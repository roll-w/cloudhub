package org.huel.cloudhub.client.disk.domain.tag.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.TagGroupDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.tag.InternalTagGroupRepository;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class TagGroupRepository extends BaseRepository<TagGroup>
        implements InternalTagGroupRepository {
    private final TagGroupDao tagGroupDao;

    public TagGroupRepository(DiskDatabase database,
                              ContextThreadAware<PageableContext> pageableContextThreadAware,
                              CacheManager cacheManager) {
        super(database.getTagGroupDao(), pageableContextThreadAware, cacheManager);
        tagGroupDao = database.getTagGroupDao();
    }

    public TagGroup getTagGroupByName(String name) {
        return cacheResult(tagGroupDao.getTagGroupByName(name));
    }

    @Override
    protected Class<TagGroup> getEntityClass() {
        return TagGroup.class;
    }

    @Override
    public List<TagGroup> findAll() {
        return get();
    }
}
