package org.huel.cloudhub.client.disk.domain.favorites.dto;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import space.lingu.NonNull;

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
) implements SystemResource, StorageIdentity {
    @Override
    public long getStorageId() {
        return storageId;
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return storageType;
    }

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
