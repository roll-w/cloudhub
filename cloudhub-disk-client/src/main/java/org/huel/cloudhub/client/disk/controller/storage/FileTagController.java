package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.tag.ContentTagService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author RollW
 */
@Api
public class FileTagController {
    private final ContentTagService contentTagService;

    public FileTagController(ContentTagService contentTagService) {
        this.contentTagService = contentTagService;
    }


    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/tags")
    public void getTags() {

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
