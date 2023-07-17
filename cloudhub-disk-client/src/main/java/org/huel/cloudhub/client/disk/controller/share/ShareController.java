package org.huel.cloudhub.client.disk.controller.share;

import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.common.ParamValidate;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.controller.storage.DownloadHelper;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.share.ShareSearchService;
import org.huel.cloudhub.client.disk.domain.share.ShareService;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareErrorCode;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareException;
import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.share.dto.ShareStructureInfo;
import org.huel.cloudhub.client.disk.controller.share.vo.ShareInfoVo;
import org.huel.cloudhub.client.disk.controller.share.vo.ShareStorageVo;
import org.huel.cloudhub.client.disk.controller.share.vo.ShareStructureVo;
import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.user.service.UserSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageOwner;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.UserErrorCode;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
@Api
public class ShareController {
    private final ShareService shareService;
    private final ShareSearchService shareSearchService;
    private final UserStorageSearchService userStorageSearchService;
    private final UserSearchService userSearchService;
    private final StorageService storageService;

    public ShareController(ShareService shareService,
                           ShareSearchService shareSearchService,
                           UserStorageSearchService userStorageSearchService,
                           UserSearchService userSearchService,
                           StorageService storageService) {
        this.shareService = shareService;
        this.shareSearchService = shareSearchService;
        this.userStorageSearchService = userStorageSearchService;
        this.userSearchService = userSearchService;
        this.storageService = storageService;
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

    @GetMapping("/users/{userId}/shares")
    public HttpResponseEntity<List<ShareInfoVo>> getOwnerShares(
            @PathVariable("userId") Long ownerId) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        List<SharePasswordInfo> sharePasswordInfos =
                shareSearchService.findByUserId(ownerId);
        boolean onlyPublic = isOnlyPublic(userIdentity, ownerId);

        return HttpResponseEntity.success(
                sharePasswordInfos.stream()
                        .filter(sharePasswordInfo ->
                                !sharePasswordInfo.isExpired(System.currentTimeMillis()))
                        .filter(sharePasswordInfo -> {
                            if (onlyPublic) {
                                return sharePasswordInfo.isPublic();
                            }
                            return true;
                        })
                        .map(ShareInfoVo::from).toList()
        );
    }

    private boolean isOnlyPublic(UserIdentity userIdentity, Long id) {
        if (userIdentity == null) {
            return true;
        }
        return userIdentity.getUserId() == id;
    }

    @GetMapping("/user/shares")
    public HttpResponseEntity<List<ShareStorageVo>> getUserShares(
            Pageable pageable) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();

        List<SharePasswordInfo> sharePasswordInfos =
                shareSearchService.findByUserId(userIdentity.getUserId(), pageable);
        // TODO: paging result
        List<? extends StorageIdentity> storageIdentities = sharePasswordInfos.stream()
                .map(sharePasswordInfo -> new SimpleStorageIdentity(
                        sharePasswordInfo.storageId(),
                        sharePasswordInfo.storageType()))
                .toList();
        List<? extends AttributedStorage> attributedStorages =
                userStorageSearchService.findStorages(storageIdentities);

        List<ShareStorageVo> shareStorageVos = pairWith(
                sharePasswordInfos,
                attributedStorages
        );
        return HttpResponseEntity.success(shareStorageVos);
    }

    private List<ShareStorageVo> pairWith(List<SharePasswordInfo> sharePasswordInfos,
                                          List<? extends AttributedStorage> attributedStorages) {
        return sharePasswordInfos.stream()
                .map(sharePasswordInfo -> {
                    AttributedStorage attributedStorage = attributedStorages.stream()
                            .filter(storage -> storage.getStorageId() == sharePasswordInfo.storageId())
                            .findFirst()
                            .orElseThrow(() -> new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND));
                    return ShareStorageVo.from(
                            sharePasswordInfo,
                            attributedStorage
                    );
                })
                .toList();
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

    @DeleteMapping("/user/shares/{shareId}")
    public HttpResponseEntity<Void> cancelShare(
            @PathVariable("shareId") Long shareId) {
        shareService.cancelShare(shareId);
        return HttpResponseEntity.success();
    }

    @GetMapping("/shares/{shareToken}/metadata")
    public HttpResponseEntity<ShareInfoVo> getShareMetadataByLink(
            @PathVariable("shareToken") String shareToken) {
        ParamValidate.notEmpty(shareToken, "shareToken");

        SharePasswordInfo sharePasswordInfo =
                shareSearchService.search(shareToken);
        if (sharePasswordInfo.expireTime() == 1) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_CANCEL);
        }
        if (sharePasswordInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
        AttributedUser attributedUser =
                userSearchService.findUser(sharePasswordInfo.creatorId());
        return HttpResponseEntity.success(
                ShareInfoVo.from(sharePasswordInfo, attributedUser)
        );
    }

    // with password
    @GetMapping("/shares/{shareToken}")
    public HttpResponseEntity<ShareStructureVo> getShareByLink(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password,
            @RequestParam(value = "parent", defaultValue = "0") Long parent) {
        ShareStructureInfo shareStructureInfo =
                shareSearchService.findStructureByShareCode(shareToken, parent);
        if (shareStructureInfo.expireTime() == 1) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_CANCEL);
        }

        if (shareStructureInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
        if (!shareStructureInfo.isPublic() &&
                !password.equals(shareStructureInfo.password())) {
            throw new UserShareException(UserShareErrorCode.ERROR_PASSWORD);
        }
        AttributedUser attributedUser =
                userSearchService.findUser(shareStructureInfo.creatorId());
        return HttpResponseEntity.success(
                ShareStructureVo.from(shareStructureInfo, attributedUser)
        );
    }

    @PostMapping("/shares/{shareToken}/save/{storageType}/{storageId}")
    public HttpResponseEntity<Void> saveShare(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password,
            @PathVariable("storageId") Long storageId,
            @PathVariable("storageType") String storageType) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        if (userIdentity == null) {
            throw new UserShareException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }

        return HttpResponseEntity.success();
    }


    @GetMapping("/shares/{shareToken}/save/{storageType}/{storageId}")
    public void getShareStorage(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password,
            @PathVariable("storageId") Long storageId,
            @PathVariable("storageType") String storageType,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {
        StorageIdentity storageIdentity =
                ParameterHelper.buildStorageIdentity(storageId, storageType);
        if (!storageIdentity.isFile()) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        SharePasswordInfo sharePasswordInfo =
                shareSearchService.search(shareToken);
        if (sharePasswordInfo.expireTime() == 1) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_CANCEL);
        }
        if (sharePasswordInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
        if (!sharePasswordInfo.isPublic() &&
                !password.equals(sharePasswordInfo.password())) {
            throw new UserShareException(UserShareErrorCode.ERROR_PASSWORD);
        }
        if (!shareService.hasStorage(sharePasswordInfo.id(), storageIdentity)) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        FileInfo fileInfo = userStorageSearchService
                .findFile(storageIdentity.getStorageId());
        DownloadHelper.downloadFile(fileInfo, httpServletRequest,
                httpServletResponse, storageService);
    }

}
