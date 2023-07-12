package org.huel.cloudhub.client.disk.domain.favorites;

import org.huel.cloudhub.client.disk.domain.favorites.dto.FavoriteGroupInfo;
import org.huel.cloudhub.client.disk.domain.favorites.dto.FavoriteItemInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

import java.util.List;

/**
 * @author RollW
 */
public interface FavoriteProvider {
    List<FavoriteGroupInfo> getFavoriteGroups();

    List<FavoriteGroupInfo> getFavoriteGroups(StorageOwner storageOwner);

    List<FavoriteItemInfo> getFavoriteItems(long favoriteGroupId);

    FavoriteGroupInfo getFavoriteGroup(long favoriteGroupId);
}
