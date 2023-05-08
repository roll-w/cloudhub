package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolderService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

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

    // TODO: Folder controller

    @GetMapping("/{type}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<Void> getFolder() {
        return HttpResponseEntity.success();
    }

    @PostMapping("/{type}/{ownerId}/disk/folder/")
    public HttpResponseEntity<Void> createFolder() {
        return HttpResponseEntity.success();
    }

    @PutMapping("/{type}/{ownerId}/disk/folder/{storageId}/name")
    public HttpResponseEntity<Void> renameFolder() {
        return HttpResponseEntity.success();
    }

    @PutMapping("/{type}/{ownerId}/disk/folder/{storageId}/move")
    public HttpResponseEntity<Void> moveFolder() {
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/{type}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<Void> deleteFolder() {
        return HttpResponseEntity.success();
    }
}
