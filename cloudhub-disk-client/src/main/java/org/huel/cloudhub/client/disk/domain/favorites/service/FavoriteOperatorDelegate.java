package org.huel.cloudhub.client.disk.domain.favorites.service;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteGroup;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;

/**
 * @author RollW
 */
public interface FavoriteOperatorDelegate {
    void updateFavoriteGroup(FavoriteGroup favoriteGroup);

    void updateFavoriteItem(FavoriteItem favoriteItem);

    long createFavoriteItem(FavoriteItem favoriteItem);

    FavoriteItem getFavoriteItemBy(long groupId, StorageIdentity storageIdentity);
}
