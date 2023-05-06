package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectoryService;
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
public class DirectoryController {
    private final UserDirectoryService directoryService;
    private final StorageActionService storageActionService;
    private final UserStorageSearchService userStorageSearchService;

    public DirectoryController(UserDirectoryService directoryService,
                               StorageActionService storageActionService,
                               UserStorageSearchService userStorageSearchService) {
        this.directoryService = directoryService;
        this.storageActionService = storageActionService;
        this.userStorageSearchService = userStorageSearchService;
    }

    // TODO: directory controller

    @GetMapping("/{type}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<Void> getDirectory() {
        return HttpResponseEntity.success();
    }

    @PostMapping("/{type}/{ownerId}/disk/folder/")
    public HttpResponseEntity<Void> createDirectory() {
        return HttpResponseEntity.success();
    }

    @PutMapping("/{type}/{ownerId}/disk/folder/{storageId}/name")
    public HttpResponseEntity<Void> renameDirectory() {
        return HttpResponseEntity.success();
    }

    @PutMapping("/{type}/{ownerId}/disk/folder/{storageId}/move")
    public HttpResponseEntity<Void> moveDirectory() {
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/{type}/{ownerId}/disk/folder/{storageId}")
    public HttpResponseEntity<Void> deleteDirectory() {
        return HttpResponseEntity.success();
    }
}
