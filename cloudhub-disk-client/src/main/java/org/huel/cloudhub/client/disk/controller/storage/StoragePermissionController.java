package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermissionService;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionsInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author RollW
 */
@Api
public class StoragePermissionController {
    private final StoragePermissionService storagePermissionService;

    public StoragePermissionController(StoragePermissionService storagePermissionService) {
        this.storagePermissionService = storagePermissionService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions")
    public HttpResponseEntity<StoragePermissionsInfo> getPermissionOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity =  ParameterHelper.buildStorageIdentity(storageId, storageType);

        StoragePermissionsInfo storagePermissionsInfo =
                storagePermissionService.getPermissionOf(storageIdentity, storageOwner, false);
        return HttpResponseEntity.success(storagePermissionsInfo);
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/public")
    public HttpResponseEntity<Void> setPublicPermissionOf(
            @PathVariable("storageType") Long storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/user/{userId}")
    public HttpResponseEntity<Void> setUserPermissionOf(
            @PathVariable("storageType") Long storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @PathVariable("userId") Long userId) {
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/user/{userId}")
    public HttpResponseEntity<Void> deleteUserPermissionOf(
            @PathVariable("storageType") Long storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @PathVariable("userId") Long userId) {
        return HttpResponseEntity.success();
    }

}
