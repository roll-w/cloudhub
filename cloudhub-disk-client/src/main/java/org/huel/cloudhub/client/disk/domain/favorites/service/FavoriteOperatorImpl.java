package org.huel.cloudhub.client.disk.domain.favorites.service;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteGroup;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteOperator;
import org.huel.cloudhub.client.disk.domain.favorites.common.FavoriteErrorCode;
import org.huel.cloudhub.client.disk.domain.favorites.common.FavoriteException;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.DataErrorCode;

/**
 * @author RollW
 */
public class FavoriteOperatorImpl implements FavoriteOperator {
    private final FavoriteOperatorDelegate delegate;

    private FavoriteGroup favoriteGroup;
    private final FavoriteGroup.Builder favoriteGroupBuilder;
    private boolean checkDeleted;

    public FavoriteOperatorImpl(FavoriteOperatorDelegate delegate,
                                FavoriteGroup favoriteGroup,
                                boolean checkDeleted) {
        this.delegate = delegate;
        this.favoriteGroup = favoriteGroup;
        this.favoriteGroupBuilder = favoriteGroup.toBuilder();
        this.checkDeleted = checkDeleted;
    }


    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        this.checkDeleted = checkDeleted;
    }

    @Override
    public boolean isCheckDeleted() {
        return checkDeleted;
    }

    @Override
    public FavoriteOperator update() throws BusinessRuntimeException {
        return this;
    }

    @Override
    public FavoriteOperator delete() throws BusinessRuntimeException {
        checkDeleted();
        favoriteGroupBuilder.setDeleted(true);
        return updateInternal();
    }

    @Override
    public FavoriteOperator rename(String newName)
            throws BusinessRuntimeException, UnsupportedOperationException {
        checkDeleted();
        favoriteGroupBuilder.setName(newName);

        return updateInternal();
    }

    @Override
    public FavoriteOperator getSystemResource() {
        return this;
    }

    @Override
    public FavoriteOperator setVisibility(boolean publicVisible) {
        checkDeleted();

        favoriteGroupBuilder.setPublic(publicVisible);

        return updateInternal();
    }

    @Override
    public FavoriteOperator addFavorite(StorageIdentity storageIdentity) {
        checkDeleted();

        FavoriteItem favoriteItem =
                delegate.getFavoriteItemBy(favoriteGroup.getId(), storageIdentity);
        if (favoriteItem != null && !favoriteItem.isDeleted()) {
            throw new BusinessRuntimeException(DataErrorCode.ERROR_DATA_EXISTED);
        }
        if (favoriteItem != null) {
            delegate.updateFavoriteItem(favoriteItem.toBuilder()
                    .setDeleted(false)
                    .setUpdateTime(System.currentTimeMillis())
                    .build()
            );
            return this;
        }

        long time = System.currentTimeMillis();
        FavoriteItem.Builder favoriteItemBuilder = FavoriteItem.builder()
                .setFavoriteGroupId(favoriteGroup.getId())
                .setStorageId(storageIdentity.getStorageId())
                .setStorageType(storageIdentity.getStorageType())
                .setCreateTime(time)
                .setUpdateTime(time);
        long id =
                delegate.createFavoriteItem(favoriteItemBuilder.build());

        return this;
    }

    @Override
    public FavoriteOperator removeFavorite(StorageIdentity storageIdentity) {
        checkDeleted();
        FavoriteItem favoriteItem =
                delegate.getFavoriteItemBy(favoriteGroup.getId(), storageIdentity);
        if (favoriteItem != null) {
            delegate.updateFavoriteItem(favoriteItem.toBuilder()
                    .setDeleted(true)
                    .setUpdateTime(System.currentTimeMillis())
                    .build()
            );
        }
        return this;
    }

    @Override
    public FavoriteOperator removeFavorite(long itemId) {
        checkDeleted();

        FavoriteItem favoriteItem = delegate.getFavoriteItem(itemId);
        if (favoriteItem != null) {
            delegate.updateFavoriteItem(favoriteItem.toBuilder()
                    .setDeleted(true)
                    .setUpdateTime(System.currentTimeMillis())
                    .build()
            );
        }
        return this;
    }

    @Override
    public FavoriteGroup getFavoriteGroup() {
        return favoriteGroup;
    }

    @Override
    public long getResourceId() {
        return favoriteGroup.getId();
    }

    private FavoriteOperator updateInternal() {
        if (favoriteGroup.getId() <= 0) {
            return this;
        }

        if (favoriteGroupBuilder != null) {
            favoriteGroup = favoriteGroupBuilder
                    .setUpdateTime(System.currentTimeMillis())
                    .build();
            delegate.updateFavoriteGroup(favoriteGroup);
        }
        return this;
    }

    private void checkDeleted() {
        if (!checkDeleted) {
            return;
        }
        if (favoriteGroup.isDeleted()) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
    }

}
