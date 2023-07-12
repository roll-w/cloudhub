package org.huel.cloudhub.client.disk.domain.favorites;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * @author RollW
 */
public interface FavoriteOperator extends SystemResourceOperator, SystemResource {
    @Override
    void setCheckDeleted(boolean checkDeleted);

    @Override
    boolean isCheckDeleted();

    @Override
    FavoriteOperator update() throws BusinessRuntimeException;

    @Override
    FavoriteOperator delete() throws BusinessRuntimeException;

    @Override
    FavoriteOperator rename(String newName) throws BusinessRuntimeException, UnsupportedOperationException;

    @Override
    FavoriteOperator getSystemResource();

    FavoriteOperator setVisibility(boolean publicVisible);

    FavoriteOperator addFavorite(StorageIdentity storageIdentity);

    FavoriteOperator removeFavorite(StorageIdentity storageIdentity);

    FavoriteGroup getFavoriteGroup();



    @Override
    long getResourceId();

    @Override
    default SystemResourceKind getSystemResourceKind() {
        return SystemResourceKind.FAVORITE_GROUP;
    }
}
