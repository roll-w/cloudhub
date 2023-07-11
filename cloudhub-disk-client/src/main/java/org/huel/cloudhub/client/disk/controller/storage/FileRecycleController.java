package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileRecycleService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.controller.storage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FileRecycleController {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageActionService storageActionService;
    private final FileRecycleService fileRecycleService;

    public FileRecycleController(UserStorageSearchService userStorageSearchService,
                                 StorageActionService storageActionService,
                                 FileRecycleService fileRecycleService) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageActionService = storageActionService;
        this.fileRecycleService = fileRecycleService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/recycles")
    public HttpResponseEntity<List<StorageVo>> getRecycleBinFiles(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type) {

        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        List<AttributedStorage> attributedStorages =
                fileRecycleService.listRecycle(storageOwner);
        List<StorageVo> storageVos = attributedStorages.stream().map(
                StorageVo::from
        ).toList();
        return HttpResponseEntity.success(storageVos);
    }
}
