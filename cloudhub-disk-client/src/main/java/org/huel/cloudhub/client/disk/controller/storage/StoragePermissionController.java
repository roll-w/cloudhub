package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermissionService;
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

    @GetMapping("/{type}/{ownerId}/disk/{storageType}/{storageId}/permission")
    public HttpResponseEntity<Void> getPermissionOf(
            @PathVariable("storageType") Long storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type) {
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @PutMapping("/{type}/{ownerId}/disk/{storageType}/{storageId}/permission/public")
    public HttpResponseEntity<Void> setPublicPermissionOf(
            @PathVariable("storageType") Long storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type) {
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @PutMapping("/{type}/{ownerId}/disk/{storageType}/{storageId}/permission/user/{userId}")
    public HttpResponseEntity<Void> setUserPermissionOf(
            @PathVariable("storageType") Long storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type,
            @PathVariable("userId") Long userId) {
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @DeleteMapping("/{type}/{ownerId}/disk/{storageType}/{storageId}/permission/user/{userId}")
    public HttpResponseEntity<Void> deleteUserPermissionOf(
            @PathVariable("storageType") Long storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type,
            @PathVariable("userId") Long userId) {
        return HttpResponseEntity.success();
    }

}
