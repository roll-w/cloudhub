package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageCategoryService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.controller.storage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FileCategoryController {
    private final StorageCategoryService storageCategoryService;

    public FileCategoryController(StorageCategoryService storageCategoryService) {
        this.storageCategoryService = storageCategoryService;
    }

    @GetMapping("{ownerType}/{ownerId}/disk/file/category/{type}")
    public HttpResponseEntity<List<StorageVo>> getByType(
            @PathVariable("ownerType") String ownerType,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        FileType fileType = FileType.from(type);

        List<? extends AttributedStorage> attributedStorages =
                storageCategoryService.getByType(storageOwner, fileType);

        return HttpResponseEntity.success(
                attributedStorages
                        .stream()
                        .filter(attributedStorage -> !attributedStorage.isDeleted())
                        .map(StorageVo::from)
                        .toList()
        );
    }

}
