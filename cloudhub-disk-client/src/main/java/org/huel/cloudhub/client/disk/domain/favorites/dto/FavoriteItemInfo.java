package org.huel.cloudhub.client.disk.domain.favorites.dto;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record FavoriteItemInfo(
        long id,
        long favoriteGroupId,
        long userId,
        long storageId,
        StorageType storageType,
        long createTime,
        long updateTime,
        boolean deleted
) implements SystemResource {
    @Override
    public long getResourceId() {
        return id;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.FAVORITE_ITEM;
    }

    public static FavoriteItemInfo of(FavoriteItem favoriteItem) {
        return new FavoriteItemInfo(
                favoriteItem.getId(),
                favoriteItem.getFavoriteGroupId(),
                favoriteItem.getUserId(),
                favoriteItem.getStorageId(),
                favoriteItem.getStorageType(),
                favoriteItem.getCreateTime(),
                favoriteItem.getUpdateTime(),
                favoriteItem.isDeleted()
        );
    }
}
