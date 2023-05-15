package org.huel.cloudhub.client.disk.controller.storage;

import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.common.ParamValidate;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.share.ShareSearchService;
import org.huel.cloudhub.client.disk.domain.share.ShareService;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareErrorCode;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareException;
import org.huel.cloudhub.client.disk.domain.share.dto.ShareInfo;
import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageOwner;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class ShareController {
    private final ShareService shareService;
    private final ShareSearchService shareSearchService;

    public ShareController(ShareService shareService,
                           ShareSearchService shareSearchService) {
        this.shareService = shareService;
        this.shareSearchService = shareSearchService;
    }

    @BuiltinOperate(BuiltinOperationType.CREATE_STORAGE_SHARE)
    @PostMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares")
    public HttpResponseEntity<SharePasswordInfo> createShare(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId,
            @RequestBody ShareCreateRequest createRequest) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        LegalUserType ownerType = LegalUserType.from(ownerTypeParam);
        StorageOwner storageOwner =
                new SimpleStorageOwner(ownerId, ownerType);
        StorageType storageType = StorageType.from(typeParam);
        StorageIdentity storageIdentity =
                new SimpleStorageIdentity(storageId, storageType);
        String password = getPassword(createRequest);
        SharePasswordInfo sharePasswordInfo = shareService.share(
                storageIdentity, storageOwner,
                createRequest.toDuration(),
                userIdentity,
                password
        );
        return HttpResponseEntity.success(sharePasswordInfo);
    }

    private String getPassword(ShareCreateRequest createRequest) {
        if (createRequest.type() == ShareCreateRequest.PUBLIC) {
            return null;
        }
        if (Strings.isNullOrEmpty(createRequest.password())) {
            return RandomStringUtils.randomAlphanumeric(ShareService.DEFAULT_PASSWORD_LENGTH);
        }
        return createRequest.password();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares")
    public HttpResponseEntity<List<SharePasswordInfo>> getStorageShares(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId) {
        return HttpResponseEntity.success();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/shares")
    public HttpResponseEntity<List<SharePasswordInfo>> getOwnerShares(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam) {
        return HttpResponseEntity.success();
    }

    @GetMapping("/user/shares")
    public HttpResponseEntity<List<SharePasswordInfo>> getUserShares(
            Pageable pageable) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();

        List<SharePasswordInfo> sharePasswordInfos =
                shareSearchService.findByUserId(userIdentity.getUserId(), pageable);
        // TODO: paging result
        return HttpResponseEntity.success(sharePasswordInfos);
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares/{shareId}")
    public HttpResponseEntity<SharePasswordInfo> getShare(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId,
            @PathVariable("shareId") Long shareId) {

        return HttpResponseEntity.success();
    }


    @DeleteMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares/{shareId}")
    public HttpResponseEntity<Void> cancelShare(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId,
            @PathVariable("shareId") Long shareId) {
        return HttpResponseEntity.success();
    }


    @GetMapping("/shares/{shareToken}/metadata")
    public HttpResponseEntity<ShareInfo> getShareMetadataByLink(
            @PathVariable("shareToken") String shareToken) {
        // basic info of share
        ParamValidate.notEmpty(shareToken, "shareToken");

        SharePasswordInfo sharePasswordInfo =
                shareSearchService.search(shareToken);
        if (sharePasswordInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
        return HttpResponseEntity.success(ShareInfo.from(sharePasswordInfo));
    }

    // with password
    @GetMapping("/shares/{shareToken}")
    public HttpResponseEntity<ShareInfo> getShareByLink(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password) {

        SharePasswordInfo sharePasswordInfo =
                shareSearchService.search(shareToken);
        if (sharePasswordInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }

        if (!password.equals(sharePasswordInfo.password())) {
            throw new UserShareException(UserShareErrorCode.ERROR_PASSWORD);
        }

        // TODO: get storage info
        return HttpResponseEntity.success(ShareInfo.from(sharePasswordInfo));
    }

    @PostMapping("/shares/{shareToken}/save/{storageType}/{storageId}")
    public HttpResponseEntity<Void> saveShare(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password,
            @PathVariable("storageId") Long storageId,
            @PathVariable("storageType") String storageType) {
        return HttpResponseEntity.success();
    }

}
