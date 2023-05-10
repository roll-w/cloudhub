package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author RollW
 */
@Api
public class StorageInfoController {
    private final UserStorageSearchService userStorageSearchService;

    public StorageInfoController(UserStorageSearchService userStorageSearchService) {
        this.userStorageSearchService = userStorageSearchService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info")
    public HttpResponseEntity<StorageVo> getStorageInfo(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type
    ) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        AttributedStorage attributedStorage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        return HttpResponseEntity.success(
                StorageVo.from(attributedStorage)
        );
    }
}
