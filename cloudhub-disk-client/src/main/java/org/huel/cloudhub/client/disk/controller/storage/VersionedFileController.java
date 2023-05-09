package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileService;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;
import org.huel.cloudhub.client.disk.domain.versioned.vo.VersionedStorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Api
public class VersionedFileController {
    private final UserStorageSearchService userStorageSearchService;
    private final UserSearchService userSearchService;
    private final VersionedFileService versionedFileService;

    public VersionedFileController(UserStorageSearchService userStorageSearchService,
                                   UserSearchService userSearchService,
                                   VersionedFileService versionedFileService) {
        this.userStorageSearchService = userStorageSearchService;
        this.userSearchService = userSearchService;
        this.versionedFileService = versionedFileService;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/versions")
    public HttpResponseEntity<List<VersionedStorageVo>> getVersionsOf(
            @PathVariable("storageType") String storageType,
            @PathVariable("storageId") Long storageId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerType) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, ownerType);
        StorageIdentity storageIdentity = ParameterHelper.buildStorageIdentity(storageId, storageType);

        if (!storageIdentity.getStorageType().isFile()) {
            return HttpResponseEntity.success(List.of());
        }
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        List<VersionedFileStorage> fileStorages =
                versionedFileService.getVersionedFileStorages(storage.getStorageId());
        if (fileStorages.isEmpty()) {
            return HttpResponseEntity.success();
        }

        List<Long> userIds = fileStorages.stream()
                .map(VersionedFileStorage::getOperator)
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);

        return HttpResponseEntity.success(
                buildVersionedStorageVos(fileStorages, attributedUsers)
        );
    }

    private List<VersionedStorageVo> buildVersionedStorageVos(
            List<VersionedFileStorage> fileStorages,
            List<? extends AttributedUser> attributedUsers) {
        List<VersionedStorageVo> versionedStorageVos = new ArrayList<>();
        for (VersionedFileStorage fileStorage : fileStorages) {
            AttributedUser attributedUser = UserSearchService.binarySearch(
                    fileStorage.getOperator(),
                    attributedUsers
            );
            VersionedStorageVo versionedStorageVo = VersionedStorageVo.of(fileStorage, attributedUser);
            versionedStorageVos.add(versionedStorageVo);
        }
        return versionedStorageVos;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/versions/{version}")
    public HttpResponseEntity<VersionedStorageVo> getVersion() {
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/versions/{version}")
    public HttpResponseEntity<Void> deleteVersion() {
        return HttpResponseEntity.success();
    }

}
