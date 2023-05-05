package org.huel.cloudhub.client.disk.domain.tag.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.ContentTagDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class ContentTagRepository extends BaseRepository<ContentTag> {
    private final ContentTagDao contentTagDao;

    protected ContentTagRepository(DiskDatabase database,
                                   CacheManager cacheManager) {
        super(database.getContentTagDao(), cacheManager);
        contentTagDao = database.getContentTagDao();
    }

    public List<ContentTag> getTagsBy(long[] ids) {
        return contentTagDao.getTagsBy(ids);
    }


}
