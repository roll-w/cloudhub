package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThread;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileInfo;
import org.huel.cloudhub.client.disk.controller.storage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class StorageManageController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageService storageService;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;

    public StorageManageController(UserStorageSearchService userStorageSearchService,
                                   StorageService storageService,
                                   ContextThreadAware<PageableContext> pageableContextThreadAware) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageService = storageService;
        this.pageableContextThreadAware = pageableContextThreadAware;
    }


    @GetMapping("/disk/storages")
    public HttpResponseEntity<List<StorageVo>> getStorages() {
        List<AttributedStorage> storages =
                userStorageSearchService.listStorages();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        List<StorageVo> storageVos = storages.stream()
                .map(StorageVo::from)
                .toList();
        return HttpResponseEntity.success(
                pageableContext.toPage(storageVos)
        );
    }

    @GetMapping("/disk/files")
    public HttpResponseEntity<List<StorageVo>> getFiles() {
        return getStorageByType(StorageType.FILE);
    }

    @GetMapping("/disk/folders")
    public HttpResponseEntity<List<StorageVo>> getFolders() {
        return getStorageByType(StorageType.FOLDER);
    }

    @NonNull
    private HttpResponseEntity<List<StorageVo>> getStorageByType(StorageType storageType) {
        List<AttributedStorage> storages =
                userStorageSearchService.listOf(storageType);

        List<StorageVo> storageVos = storages.stream()
                .map(StorageVo::from)
                .toList();
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        return HttpResponseEntity.success(
                pageableContext.toPage(storageVos)
        );
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
        if (storageIdentity.getStorageType().isFile()) {
            FileInfo fileInfo = userStorageSearchService.findFile(
                    storageIdentity.getStorageId(), storageOwner
            );
            long size = storageService.getFileSize(fileInfo.getFileId());
            return HttpResponseEntity.success(
                    StorageVo.from(fileInfo, size, fileInfo.getFileId())
            );
        }
        AttributedStorage attributedStorage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        return HttpResponseEntity.success(
                StorageVo.from(attributedStorage)
        );
    }

}
