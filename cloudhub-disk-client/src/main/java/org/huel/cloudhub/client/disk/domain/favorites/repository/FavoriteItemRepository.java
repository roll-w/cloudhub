package org.huel.cloudhub.client.disk.domain.favorites.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.FavoriteItemDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author RollW
 */
@Repository
public class FavoriteItemRepository extends BaseRepository<FavoriteItem> {
    private final FavoriteItemDao favoriteItemDao;

    protected FavoriteItemRepository(DiskDatabase database,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware,
                                     CacheManager cacheManager) {
        super(database.getFavoriteItemDao(),
                pageableContextThreadAware, cacheManager);
        this.favoriteItemDao = database.getFavoriteItemDao();
    }

    @Override
    protected Class<FavoriteItem> getEntityClass() {
        return FavoriteItem.class;
    }

    public List<FavoriteItem> getByGroup(long groupId) {
        return cacheResult(favoriteItemDao.getByGroup(groupId));
    }

    public FavoriteItem getByGroupAndIdentity(long groupId, StorageIdentity storageIdentity) {
        return cacheResult(favoriteItemDao.getByGroupAndIdentity(groupId, storageIdentity));
    }
}
