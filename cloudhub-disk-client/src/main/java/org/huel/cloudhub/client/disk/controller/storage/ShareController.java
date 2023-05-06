package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.share.ShareService;
import org.huel.cloudhub.client.disk.domain.share.dto.ShareInfo;
import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class ShareController {
    private final ShareService shareService;
    private final UserStorageSearchService userStorageSearchService;

    public ShareController(ShareService shareService,
                           UserStorageSearchService userStorageSearchService) {
        this.shareService = shareService;
        this.userStorageSearchService = userStorageSearchService;
    }


    @PostMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares")
    public HttpResponseEntity<SharePasswordInfo> createShare(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId) {
        LegalUserType ownerType = LegalUserType.from(ownerTypeParam);
        StorageType storageType = StorageType.from(typeParam);
        StorageIdentity storageIdentity =
                new SimpleStorageIdentity(storageId, storageType);

        SharePasswordInfo sharePasswordInfo = shareService.share(
                storageIdentity, ShareService.DAYS_30,
                null, null);

        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares")
    public HttpResponseEntity<List<SharePasswordInfo>> getStorageShares() {
        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/shares")
    public HttpResponseEntity<List<SharePasswordInfo>> getOwnerShares() {
        return HttpResponseEntity.success();
    }

    @GetMapping("/user/shares")
    public HttpResponseEntity<List<SharePasswordInfo>> getUsersShares() {
        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares/{shareId}")
    public HttpResponseEntity<SharePasswordInfo> getShare() {
        return HttpResponseEntity.success();
    }


    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares/{shareId}")
    public HttpResponseEntity<Void> cancelShare() {
        return HttpResponseEntity.success();
    }


    @GetMapping("/shares/{shareToken}/metadata")
    public HttpResponseEntity<ShareInfo> getShareMetadataByLink(
            @PathVariable("shareToken") String shareToken) {
        // basic info of share
        return HttpResponseEntity.success();
    }

    // with password
    @GetMapping("/shares/{shareToken}")
    public HttpResponseEntity<ShareInfo> getShareByLink(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password) {
        return HttpResponseEntity.success();
    }

}
