package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.OneParameterRequest;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.storagepermission.PermissionType;
import org.huel.cloudhub.client.disk.domain.storagepermission.PublicPermissionType;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermissionAction;
import org.huel.cloudhub.client.disk.domain.storagepermission.StoragePermissionService;
import org.huel.cloudhub.client.disk.domain.storagepermission.dto.StoragePermissionsInfo;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceAuthenticate;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperatorProvider;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.service.UserProvider;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class StoragePermissionController {
    private final StoragePermissionService storagePermissionService;
    private final UserStorageSearchService userStorageSearchService;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;
    private final UserProvider userProvider;

    public StoragePermissionController(StoragePermissionService storagePermissionService,
                                       UserStorageSearchService userStorageSearchService,
                                       SystemResourceOperatorProvider systemResourceOperatorProvider,
                                       UserProvider userProvider) {
        this.storagePermissionService = storagePermissionService;
        this.userStorageSearchService = userStorageSearchService;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
        this.userProvider = userProvider;
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
    @SystemResourceAuthenticate(
            idParam = "storageId",
            kindParam = "storageType",
            inferredKind = false
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/public")
    public HttpResponseEntity<Void> setPublicPermissionOf(
            @PathVariable("storageId") Long storageId,
            @PathVariable("storageType") String storageType,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody OneParameterRequest<PublicPermissionType> request) {
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        StoragePermissionAction storagePermissionAction = systemResourceOperatorProvider.getSystemResourceOperator(
                storage,
                SystemResourceKind.STORAGE_PERMISSION,
                true
        );
        storagePermissionAction.setPermission(request.value());

        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @SystemResourceAuthenticate(
            idParam = "storageId",
            kindParam = "storageType",
            inferredKind = false
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/user/{userId}")
    public HttpResponseEntity<Void> setUserPermissionOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @PathVariable("userId") String userIdentity,
            @RequestBody OneParameterRequest<List<PermissionType>> permissionTypes) {
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        AttributedUser user = userProvider.tryFindUser(userIdentity);
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);

        StoragePermissionAction storagePermissionAction = systemResourceOperatorProvider.getSystemResourceOperator(
                storage,
                SystemResourceKind.STORAGE_PERMISSION,
                true
        );
        storagePermissionAction.setUserPermission(user, permissionTypes.value());
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.UPDATE_STORAGE_PERMISSION)
    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/permissions/user/{userId}")
    public HttpResponseEntity<Void> deleteUserPermissionOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @PathVariable("userId") String userIdentity) {
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        AttributedUser user = userProvider.tryFindUser(userIdentity);
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        StoragePermissionAction storagePermissionAction = systemResourceOperatorProvider.getSystemResourceOperator(
                storage,
                SystemResourceKind.STORAGE_PERMISSION,
                true
        );
        storagePermissionAction.removeUserPermission(user);
        return HttpResponseEntity.success();
    }

}
