package org.huel.cloudhub.client.disk.domain.favorites.service;

import org.huel.cloudhub.client.disk.domain.favorites.FavoriteGroup;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteItem;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteProvider;
import org.huel.cloudhub.client.disk.domain.favorites.FavoriteService;
import org.huel.cloudhub.client.disk.domain.favorites.common.FavoriteErrorCode;
import org.huel.cloudhub.client.disk.domain.favorites.common.FavoriteException;
import org.huel.cloudhub.client.disk.domain.favorites.dto.FavoriteGroupInfo;
import org.huel.cloudhub.client.disk.domain.favorites.dto.FavoriteItemInfo;
import org.huel.cloudhub.client.disk.domain.favorites.repository.FavoriteGroupRepository;
import org.huel.cloudhub.client.disk.domain.favorites.repository.FavoriteItemRepository;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceProvider;
import org.huel.cloudhub.client.disk.domain.systembased.UnsupportedKindException;
import org.huel.cloudhub.client.disk.domain.systembased.validate.FieldType;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.systembased.validate.ValidatorProvider;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class FavoriteServiceImpl implements
        FavoriteService, FavoriteProvider, SystemResourceProvider {
    private final FavoriteGroupRepository favoriteGroupRepository;
    private final FavoriteItemRepository favoriteItemRepository;
    private final Validator validator;

    public FavoriteServiceImpl(FavoriteGroupRepository favoriteGroupRepository,
                               FavoriteItemRepository favoriteItemRepository,
                               ValidatorProvider validatorProvider) {
        this.favoriteGroupRepository = favoriteGroupRepository;
        this.favoriteItemRepository = favoriteItemRepository;
        this.validator = validatorProvider.getValidator(SystemResourceKind.FAVORITE_GROUP);
    }

    @Override
    public List<FavoriteGroupInfo> getFavoriteGroups() {
        return favoriteGroupRepository.get()
                .stream()
                .map(FavoriteGroupInfo::of)
                .toList();
    }

    @Override
    public List<FavoriteGroupInfo> getFavoriteGroups(StorageOwner storageOwner) {
        return List.of();
    }

    @Override
    public List<FavoriteItemInfo> getFavoriteItems(
            long favoriteGroupId) {
        return List.of();
    }

    @Override
    public FavoriteGroupInfo getFavoriteGroup(long favoriteGroupId) {
        if (favoriteGroupId == 0) {
            return FavoriteGroupInfo.DEFAULT;
        }
        if (favoriteGroupId == -1) {
            // TODO: integrate with recycle bin
            return FavoriteGroupInfo.RECYCLE_BIN;
        }

        FavoriteGroup favoriteGroup =
                favoriteGroupRepository.getById(favoriteGroupId);
        if (favoriteGroup == null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return FavoriteGroupInfo.of(favoriteGroup);
    }

    public FavoriteItemInfo getFavoriteItem(long itemId) {
        FavoriteItem favoriteItem = favoriteItemRepository.getById(itemId);
        if (favoriteItem == null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_NOT_FOUND);
        }
        return FavoriteItemInfo.of(favoriteItem);
    }

    @Override
    public void createFavoriteGroup(String name,
                                    boolean isPublic,
                                    Operator of) {
        validator.validateThrows(name, FieldType.NAME);
        FavoriteGroup existed =
                favoriteGroupRepository.getByName(name, of);
        if (existed != null) {
            throw new FavoriteException(FavoriteErrorCode.ERROR_FAVORITE_EXISTED);
        }
        long now = System.currentTimeMillis();
        FavoriteGroup favoriteGroup = FavoriteGroup.builder()
                .setName(name)
                .setUserId(of.getOperatorId())
                .setPublic(isPublic)
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        favoriteGroupRepository.insert(favoriteGroup);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FAVORITE_GROUP ||
                systemResourceKind == SystemResourceKind.FAVORITE_ITEM;
    }

    @Override
    public SystemResource provide(long resourceId,
                                  SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException {
        return switch (systemResourceKind) {
            case FAVORITE_GROUP -> getFavoriteGroup(resourceId);
            case FAVORITE_ITEM -> getFavoriteItem(resourceId);
            default -> throw new UnsupportedKindException(systemResourceKind);
        };
    }
}
