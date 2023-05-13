package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.common.ParamValidate;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolderService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderStructureInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FolderController {
    private final UserFolderService userFolderService;
    private final StorageActionService storageActionService;
    private final UserStorageSearchService userStorageSearchService;

    public FolderController(UserFolderService userFolderService,
                            StorageActionService storageActionService,
                            UserStorageSearchService userStorageSearchService) {
        this.userFolderService = userFolderService;
        this.storageActionService = storageActionService;
        this.userStorageSearchService = userStorageSearchService;
    }

    @BuiltinOperate(BuiltinOperationType.CREATE_FOLDER)
    @PostMapping("/{ownerType}/{ownerId}/disk/folder/{folderId}")
    public HttpResponseEntity<StorageVo> createFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("folderId") Long folderId,
            @RequestBody FolderCreateRequest folderCreateRequest) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        ParamValidate.notEmpty(folderCreateRequest.name(), "folder name");
        AttributedStorage storage = userFolderService.createFolder(
                folderCreateRequest.name(),
                folderId, storageOwner
        );
        return HttpResponseEntity.success(
                StorageVo.from(storage)
        );
    }

    @BuiltinOperate(BuiltinOperationType.RENAME_FOLDER)
    @PutMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}/name")
    public HttpResponseEntity<Void> renameFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("storageId") Long storageId) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.MOVE_FOLDER)
    @PutMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}/move")
    public HttpResponseEntity<Void> moveFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("storageId") Long storageId) {
        return HttpResponseEntity.success();
    }

    @BuiltinOperate(BuiltinOperationType.DELETE_FOLDER)
    @DeleteMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<Void> deleteFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("storageId") Long storageId) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = new SimpleStorageIdentity(storageId, StorageType.FOLDER);

        AttributedStorage storage = userStorageSearchService.findStorage(storageIdentity, storageOwner);
        if (storage.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_ALREADY_DELETED);
        }
        StorageAction storageAction =
                storageActionService.openStorageAction(storage);
        storageAction.delete();
        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<List<StorageVo>> listFiles(
            @PathVariable("storageId") Long directory,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {
        List<AttributedStorage> storages = userStorageSearchService.listFiles(
                directory,
                new SimpleStorageOwner(ownerId, LegalUserType.from(type))
        );

        return HttpResponseEntity.success(
                storages.stream()
                        .filter(storage -> !storage.isDeleted())
                        .map(StorageVo::from)
                        .toList()
        );
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}/info")
    public HttpResponseEntity<FolderStructureInfo> getFolderInfo(
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type
    ) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        StorageIdentity storageIdentity = new SimpleStorageIdentity(storageId, StorageType.FOLDER);
        FolderStructureInfo folderStructureInfo =
                userStorageSearchService.findFolder(storageIdentity.getStorageId(), storageOwner);
        return HttpResponseEntity.success(
                folderStructureInfo
        );
    }
}
