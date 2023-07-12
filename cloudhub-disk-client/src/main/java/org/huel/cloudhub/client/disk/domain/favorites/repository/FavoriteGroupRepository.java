package org.huel.cloudhub.client.disk.domain.favorites.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.FavoriteGroupDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteGroup;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class FavoriteGroupRepository extends BaseRepository<FavoriteGroup> {
    private final FavoriteGroupDao favoriteGroupDao;

    protected FavoriteGroupRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getFavoriteGroupDao(),
                pageableContextThreadAware, cacheManager);
        this.favoriteGroupDao = database.getFavoriteGroupDao();
    }

    @Override
    protected Class<FavoriteGroup> getEntityClass() {
        return FavoriteGroup.class;
    }

    public FavoriteGroup getByName(String name, Operator operator) {
        return cacheResult(
                favoriteGroupDao.getByName(name, operator)
        );
    }
}
