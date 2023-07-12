package org.huel.cloudhub.client.disk.domain.favorites.service;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteGroup;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteOperator;
import org.huel.cloudhub.client.disk.domain.favorites.common.FavoriteErrorCode;
import org.huel.cloudhub.client.disk.domain.favorites.common.FavoriteException;
import org.huel.cloudhub.client.disk.domain.favorites.repository.FavoriteGroupRepository;
import org.huel.cloudhub.client.disk.domain.favorites.repository.FavoriteItemRepository;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperatorFactory;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class FavoriteOperatorFactoryService implements
        SystemResourceOperatorFactory, FavoriteOperatorDelegate {
    private final FavoriteGroupRepository favoriteGroupRepository;
    private final FavoriteItemRepository favoriteItemRepository;

    public FavoriteOperatorFactoryService(FavoriteGroupRepository favoriteGroupRepository,
                                          FavoriteItemRepository favoriteItemRepository) {
        this.favoriteGroupRepository = favoriteGroupRepository;
        this.favoriteItemRepository = favoriteItemRepository;
    }

    @Override
    public void updateFavoriteGroup(FavoriteGroup favoriteGroup) {
        favoriteGroupRepository.update(favoriteGroup);
    }

    @Override
    public void updateFavoriteItem(FavoriteItem favoriteItem) {
        favoriteItemRepository.update(favoriteItem);
    }

    @Override
    public long createFavoriteItem(FavoriteItem favoriteItem) {
        return favoriteItemRepository.insert(favoriteItem);
    }

    @Override
    public FavoriteItem getFavoriteItemBy(long groupId,
                                          StorageIdentity storageIdentity) {
        return null;
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FAVORITE_GROUP;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return FavoriteOperator.class.isAssignableFrom(clazz);
    }

    @Override
    public SystemResourceOperator createResourceOperator(SystemResource systemResource,
                                                         boolean checkDelete) {
        if (systemResource instanceof FavoriteGroup favoriteGroup) {
            return new FavoriteOperatorImpl(
                    this, favoriteGroup, checkDelete);
        }
        FavoriteGroup favoriteGroup = favoriteGroupRepository
                .getById(systemResource.getResourceId());
        if (favoriteGroup == null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return new FavoriteOperatorImpl(
                this, favoriteGroup, checkDelete);
    }
}
