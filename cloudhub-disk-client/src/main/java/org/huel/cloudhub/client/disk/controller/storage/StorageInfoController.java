package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.common.ParamValidate;
import org.huel.cloudhub.client.disk.common.ParameterFailedException;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author RollW
 */
@Api
public class StorageInfoController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageActionService storageActionService;

    public StorageInfoController(UserStorageSearchService userStorageSearchService,
                                 StorageActionService storageActionService) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageActionService = storageActionService;
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

    public record LongActionRequest(
            Long value
    ) {
    }

    public record StringActionRequest(
            String value
    ) {
    }
}
