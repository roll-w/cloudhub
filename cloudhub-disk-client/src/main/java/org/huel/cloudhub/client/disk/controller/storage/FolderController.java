package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.common.ParamValidate;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderStructureInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FolderController {
    private final UserFolderService userFolderService;
    private final StorageActionService storageActionService;
    private final UserStorageSearchService userStorageSearchService;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;
    private final SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory;

    public FolderController(UserFolderService userFolderService,
                            StorageActionService storageActionService,
                            UserStorageSearchService userStorageSearchService,
                            ContextThreadAware<PageableContext> pageableContextThreadAware,
                            SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory) {
        this.userFolderService = userFolderService;
        this.storageActionService = storageActionService;
        this.userStorageSearchService = userStorageSearchService;
        this.pageableContextThreadAware = pageableContextThreadAware;
        this.systemResourceAuthenticationProviderFactory = systemResourceAuthenticationProviderFactory;
    }

    @SystemResourceAuthenticate(idParam = "folderId")
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

    @SystemResourceAuthenticate(idParam = "storageId")
    @BuiltinOperate(BuiltinOperationType.RENAME_FOLDER)
    @PutMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}/name")
    public HttpResponseEntity<Void> renameFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("storageId") Long storageId) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);

        return HttpResponseEntity.success();
    }

    @SystemResourceAuthenticate(idParam = "storageId")
    @BuiltinOperate(BuiltinOperationType.MOVE_FOLDER)
    @PutMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}/move")
    public HttpResponseEntity<Void> moveFolder(
            @PathVariable("ownerType") String type,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("storageId") Long storageId) {
        return HttpResponseEntity.success();
    }

    @SystemResourceAuthenticate(idParam = "storageId")
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

    @SystemResourceAuthenticate(
            idParam = "directory",
            kind = SystemResourceKind.FOLDER, inferredKind = false,
            action = Action.ACCESS, inferredAction = false
    )
    @GetMapping("/{ownerType}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<List<StorageVo>> listFiles(
            @PathVariable("storageId") Long directory,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        pageableContext.setIncludeDeleted(false);

        List<AttributedStorage> storages = userStorageSearchService.listFiles(
                directory,
                storageOwner
        );
        SystemResourceAuthenticationProvider systemAuthenticationProvider =
                systemResourceAuthenticationProviderFactory.getSystemResourceAuthenticationProvider(SystemResourceKind.FILE);

        List<SystemAuthentication> systemAuthentications =
                systemAuthenticationProvider.authenticate(storages, userIdentity, Action.ACCESS);
        List<AttributedStorage> authenticatedStorages =
                systemAuthentications.stream()
                        .filter(SystemAuthentication::isAllowAccess)
                        .map(SystemAuthentication::getSystemResource)
                        .map(resource -> resource.cast(AttributedStorage.class))
                        .toList();

        return HttpResponseEntity.success(
                authenticatedStorages.stream()
                        .filter(storage -> !storage.isDeleted())
                        .map(StorageVo::from)
                        .toList()
        );
    }

    @SystemResourceAuthenticate(
            idParam = "storageId",
            kind = SystemResourceKind.FOLDER, inferredKind = false,
            action = Action.ACCESS, inferredAction = false
    )
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
