package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author RollW
 */
@Api
public class VersionedFileController {
    private final VersionedFileService versionedFileService;

    public VersionedFileController(VersionedFileService versionedFileService) {
        this.versionedFileService = versionedFileService;
    }


    @GetMapping("/{ownerType}/{ownerId}/disk/file/{storageId}/versions")
    public void getVersionsOf() {
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/file/{storageId}/versions/{version}")
    public void getVersion() {
    }

    @DeleteMapping("/{ownerType}/{ownerId}/disk/file/{storageId}/versions/{version}")
    public void deleteVersion() {
    }

}
