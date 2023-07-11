package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.common.ParamValidate;
import org.huel.cloudhub.client.disk.common.ParameterFailedException;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.LongActionRequest;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.controller.StringActionRequest;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceAuthenticate;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileInfo;
import org.huel.cloudhub.client.disk.controller.storage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Api
public class StorageInfoController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageService storageService;
    private final StorageActionService storageActionService;

    public StorageInfoController(UserStorageSearchService userStorageSearchService,
                                 StorageService storageService,
                                 StorageActionService storageActionService) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageService = storageService;
        this.storageActionService = storageActionService;
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.ACCESS,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info")
    public HttpResponseEntity<StorageVo> getStorageInfo(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type
    ) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        return HttpResponseEntity.success(
                getStorageVo(storageOwner, storageIdentity)
        );
    }

    @NonNull
    private StorageVo getStorageVo(StorageOwner storageOwner,
                                   StorageIdentity storageIdentity) {
        if (storageIdentity.getStorageType().isFile()) {
            FileInfo fileInfo = userStorageSearchService.findFile(
                    storageIdentity.getStorageId(), storageOwner
            );
            long size = storageService.getFileSize(fileInfo.getFileId());
            return StorageVo.from(fileInfo, size);
        }
        AttributedStorage attributedStorage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        return StorageVo.from(attributedStorage);
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.RENAME,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info/name")
    public HttpResponseEntity<Void> renameStorage(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody StringActionRequest actionRequest
    ) {
        ParamValidate.notEmpty(actionRequest.value(), "名称不能为空");

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        BuiltinOperationType builtinOperationType = switch (storageIdentity.getStorageType()) {
            case FOLDER -> BuiltinOperationType.RENAME_FOLDER;
            case FILE -> BuiltinOperationType.RENAME_FILE;
            default -> throw new ParameterFailedException("Unexpected value: " + storageIdentity.getStorageType());
        };
        OperationContextHolder
                .getContext()
                .setOperateType(builtinOperationType);
        StorageAction storageAction = storageActionService
                .openStorageAction(storageIdentity, storageOwner);
        storageAction.rename(actionRequest.value());

        return HttpResponseEntity.success();
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.MOVE,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info/parent")
    public HttpResponseEntity<Void> moveStorage(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody LongActionRequest actionRequest

    ) {
        ParamValidate.notNull(actionRequest.value(), "目标文件夹不能为空");

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        BuiltinOperationType builtinOperationType = switch (storageIdentity.getStorageType()) {
            case FOLDER -> BuiltinOperationType.MOVE_FOLDER;
            case FILE -> BuiltinOperationType.MOVE_FILE;
            default -> throw new ParameterFailedException(
                    "Unexpected value: " + storageIdentity.getStorageType());
        };
        OperationContextHolder
                .getContext()
                .setOperateType(builtinOperationType);
        StorageAction storageAction = storageActionService
                .openStorageAction(storageIdentity, storageOwner);
        storageAction.move(actionRequest.value());
        return HttpResponseEntity.success();
    }

    @SystemResourceAuthenticate(
            inferredAction = false, action = Action.COPY,
            inferredKind = false, kindParam = "storageType",
            idParam = "storageId"
    )
    @PostMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/info/copy")
    public HttpResponseEntity<Void> copyStorageTo(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestBody LongActionRequest actionRequest
    ) {
        ParamValidate.notNull(actionRequest.value(), "目标文件夹不能为空");

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        BuiltinOperationType builtinOperationType = switch (storageIdentity.getStorageType()) {
            case FOLDER -> BuiltinOperationType.COPY_FOLDER;
            case FILE -> BuiltinOperationType.COPY_FILE;
            default -> throw new ParameterFailedException(
                    "Unexpected value: " + storageIdentity.getStorageType());
        };
        OperationContextHolder
                .getContext()
                .setOperateType(builtinOperationType);
        StorageAction storageAction = storageActionService
                .openStorageAction(storageIdentity, storageOwner);
        storageAction.copy(actionRequest.value());
        return HttpResponseEntity.success();
    }

}
