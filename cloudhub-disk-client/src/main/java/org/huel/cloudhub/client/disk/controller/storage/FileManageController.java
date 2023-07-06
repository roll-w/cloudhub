package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThread;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class FileManageController {
    private final UserStorageSearchService userStorageSearchService;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;

    public FileManageController(UserStorageSearchService userStorageSearchService,
                                ContextThreadAware<PageableContext> pageableContextThreadAware) {
        this.userStorageSearchService = userStorageSearchService;
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

}
