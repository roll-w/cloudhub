package org.huel.cloudhub.client.disk.domain.tag.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.ContentTagDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class ContentTagRepository extends BaseRepository<ContentTag> {
    private final ContentTagDao contentTagDao;

    protected ContentTagRepository(DiskDatabase database,
                                   ContextThreadAware<PageableContext> pageableContextThreadAware,
                                   CacheManager cacheManager) {
        super(database.getContentTagDao(), pageableContextThreadAware, cacheManager);
        contentTagDao = database.getContentTagDao();
    }

    @Override
    protected Class<ContentTag> getEntityClass() {
        return ContentTag.class;
    }

    public ContentTag getByName(String name) {
        ContentTag contentTag =
                contentTagDao.getByName(name);
        return cacheResult(contentTag);
    }
}
