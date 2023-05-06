package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageCategoryService;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FileStorageSearchController {
    private final StorageCategoryService storageCategoryService;

    public FileStorageSearchController(StorageCategoryService storageCategoryService) {
        this.storageCategoryService = storageCategoryService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/files/{type}")
    public HttpResponseEntity<List<StorageVo>> getByType(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("type") String typeParam) {
        LegalUserType ownerType = LegalUserType.from(ownerTypeParam);
        FileType fileType = FileType.from(typeParam);

        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/files")
    public HttpResponseEntity<List<StorageVo>> getAllFiles(@PathVariable("ownerId") Long ownerId,
                                                           @PathVariable("ownerType") String ownerTypeParam) {
        LegalUserType ownerType = LegalUserType.from(ownerTypeParam);

        return HttpResponseEntity.success();
    }

}
