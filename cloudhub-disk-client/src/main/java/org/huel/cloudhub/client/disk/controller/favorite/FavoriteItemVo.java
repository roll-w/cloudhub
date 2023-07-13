package org.huel.cloudhub.client.disk.controller.favorite;

import org.huel.cloudhub.client.disk.domain.favorites.dto.FavoriteItemInfo;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record FavoriteItemVo(
        long id,
        long favoriteGroupId,
        long userId,
        long ownerId,
        LegalUserType ownerType,
        String name,
        long storageId,
        StorageType storageType,
        long createTime,
        long updateTime
) {

    public static FavoriteItemVo of(FavoriteItemInfo favoriteItemInfo,
                                    AttributedStorage storage) {
        return new FavoriteItemVo(
                favoriteItemInfo.id(),
                favoriteItemInfo.favoriteGroupId(),
                favoriteItemInfo.userId(),
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getName(),
                favoriteItemInfo.storageId(),
                favoriteItemInfo.storageType(),
                favoriteItemInfo.createTime(),
                favoriteItemInfo.updateTime()
        );
    }
}
