package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAttributesService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageTagValue;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class FileTagController {
    private final StorageAttributesService storageAttributesService;

    public FileTagController(StorageAttributesService storageAttributesService) {
        this.storageAttributesService = storageAttributesService;
    }


    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public HttpResponseEntity<List<StorageTagValue>> getTags(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerTypeParam);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);
        List<StorageTagValue> storageTagValues =
                storageAttributesService.getStorageTags(storageIdentity, storageOwner);
        return HttpResponseEntity.success(storageTagValues);
    }


    @PutMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public void resetTags() {

    }

    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags/{metaId}")
    public void deleteTag() {

    }

    @PostMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public void createTag() {
    }

}
