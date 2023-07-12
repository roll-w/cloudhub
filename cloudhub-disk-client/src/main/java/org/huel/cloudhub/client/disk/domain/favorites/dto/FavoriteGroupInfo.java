package org.huel.cloudhub.client.disk.domain.favorites.dto;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteGroup;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public record FavoriteGroupInfo(
        long id,
        String name,
        long userId,
        boolean isPublic,
        long createTime,
        long updateTime,
        boolean deleted
) implements SystemResource {
    public static final FavoriteGroupInfo DEFAULT =
            of(FavoriteGroup.SYSTEM_FAVORITE_GROUP);
    public static final FavoriteGroupInfo RECYCLE_BIN =
            of(FavoriteGroup.RECYCLE_BIN);

    public static FavoriteGroupInfo of(FavoriteGroup favoriteGroup) {
        return new FavoriteGroupInfo(
                favoriteGroup.getId(),
                favoriteGroup.getName(),
                favoriteGroup.getUserId(),
                favoriteGroup.isPublic(),
                favoriteGroup.getCreateTime(),
                favoriteGroup.getUpdateTime(),
                favoriteGroup.isDeleted()
        );
    }

    @Override
    public long getResourceId() {
        return id;
    }

    @Override
    public SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.FAVORITE_GROUP;
    }
}
