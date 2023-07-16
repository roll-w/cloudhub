package org.huel.cloudhub.client.disk.domain.share.service;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.share.ShareSearchService;
import org.huel.cloudhub.client.disk.domain.share.ShareService;
import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareErrorCode;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareException;
import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.share.dto.ShareStructureInfo;
import org.huel.cloudhub.client.disk.domain.share.repository.UserShareRepository;
import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderStructureInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.web.data.page.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class ShareServiceImpl implements ShareService, ShareSearchService,
        SystemResourceActionProvider {
    private static final Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

    private final UserShareRepository userShareRepository;
    private final UserStorageSearchService userStorageSearchService;

    public ShareServiceImpl(UserShareRepository userShareRepository,
                            UserStorageSearchService userStorageSearchService) {
        this.userShareRepository = userShareRepository;
        this.userStorageSearchService = userStorageSearchService;
    }

    @Override
    public SharePasswordInfo share(StorageIdentity storageIdentity,
                                   StorageOwner storageOwner,
                                   Duration time,
                                   Operator operator,
                                   String password) {
        validatePassword(password);

        StringBuilder sb = new StringBuilder();
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        if (storage.isDeleted()) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        String hash = calcHash(storage);

        String random = RandomStringUtils.randomAlphanumeric(15);
        String shareId = sb.append(hash).append("_").append(random).toString();

        long now = System.currentTimeMillis();
        long expireTime = time.isNegative()
                ? 0
                : time.toMillis() + now;

        UserShare share = UserShare.builder()
                .setShareId(shareId)
                .setCreateTime(now)
                .setUpdateTime(now)
                .setExpireTime(expireTime)
                .setStorageId(storage.getStorageId())
                .setStorageType(storage.getStorageType())
                .setUserId(operator.getOperatorId())
                .setPassword(password)
                .build();
        long id = userShareRepository.insert(share);
        UserShare inserted = share
                .toBuilder()
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .addSystemResource(storage)
                .setChangedContent(shareId);
        logger.info("shareId: {}", shareId);
        return SharePasswordInfo.from(inserted);
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return;
        }
        if (password.length() != 6) {
            throw new UserShareException(UserShareErrorCode.ERROR_PASSWORD_FORMAT);
        }
    }

    private String calcHash(Storage storage) {
        long ownerId = storage.getOwnerId();
        LegalUserType userType = storage.getOwnerType();
        int ordinal = userType.ordinal();
        long hashCal = ownerId << (ownerId | (ordinal * 10));
        String hash = Hashing.goodFastHash(24).hashLong(hashCal).toString();
        String nameHash = Hashing.goodFastHash(24).hashString(
                        storage.getName() + storage.getStorageType(), StandardCharsets.UTF_8)
                .toString();
        return hash.substring(0, 8) +
                nameHash.substring(0, 8);
    }

    @Override
    public void cancelShare(long shareId) {
        UserShare share = userShareRepository.getById(shareId);
        checkShareStatus(share);
        UserShare updated = share.toBuilder()
                .setUpdateTime(System.currentTimeMillis())
                .setExpireTime(1)
                .build();
        userShareRepository.update(updated);
        OperationContextHolder.getContext()
                .addSystemResource(share)
                .setChangedContent(share.getShareId());
    }

    @Override
    public boolean hasStorage(long shareId,
                              StorageIdentity storageIdentity) {
        UserShare share = userShareRepository.getById(shareId);
        checkShareStatus(share);
        if (share.getStorageId() == storageIdentity.getStorageId() &&
                share.getStorageType() == storageIdentity.getStorageType()) {
            return true;
        }

        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity);
        if (share.getStorageType() == StorageType.FOLDER &&
                storage.getParentId() == share.getStorageId()) {
            return true;
        }
        if (storage.isDeleted()) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        if (storage.getParentId() == 0) {
            return false;
        }
        FolderStructureInfo folderStructureInfo =
                userStorageSearchService.findFolder(storage.getParentId());
        List<FolderInfo> folderInfos = folderStructureInfo.getParents();
        for (FolderInfo folderInfo : folderInfos) {
            if (folderInfo.getStorageId() == storage.getParentId()) {
                return true;
            }
        }
        return false;
    }

    private static void checkShareStatus(UserShare share) {
        if (share == null) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_NOT_FOUND);
        }
        if (share.getExpireTime() != 0 && share.getExpireTime() < System.currentTimeMillis()) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
    }

    @NonNull
    @Override
    public SystemAuthentication authenticate(SystemResource systemResource,
                                             Operator operator, Action action) {
        UserShare userShare =
                userShareRepository.getById(systemResource.getResourceId());
        if (userShare == null) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_NOT_FOUND);
        }
        if (userShare.getUserId() == operator.getOperatorId()) {
            return new SimpleSystemAuthentication(userShare, operator, true);
        }
        checkShareStatus(userShare);
        if (action.isRead()) {
            return new SimpleSystemAuthentication(userShare, operator, true);
        }
        return new SimpleSystemAuthentication(userShare, operator, false);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.STORAGE_SHARE;
    }

    @Override
    public SystemResource provide(long resourceId,
                                  SystemResourceKind systemResourceKind) {
        if (systemResourceKind != SystemResourceKind.STORAGE_SHARE) {
            throw new UnsupportedKindException(systemResourceKind);
        }
        return userShareRepository.getById(resourceId);
    }

    @Override
    public SharePasswordInfo search(String shareCode) {
        UserShare userShare =
                findShareByShareCode(shareCode);
        return SharePasswordInfo.from(userShare);
    }

    @Override
    public SharePasswordInfo findById(long shareId) {
        UserShare userShare = findShareById(shareId);
        return SharePasswordInfo.from(userShare);
    }

    @Override
    public List<SharePasswordInfo> findByUserId(long userId,
                                                Pageable pageable) {
        List<UserShare> userShares =
                userShareRepository.getByUserId(userId, pageable.toOffset());
        return userShares.stream()
                .map(SharePasswordInfo::from)
                .toList();
    }

    @Override
    public List<SharePasswordInfo> findByUserId(long userId) {
        List<UserShare> userShares =
                userShareRepository.getByUserId(userId);
        return userShares.stream()
                .map(SharePasswordInfo::from)
                .toList();
    }

    @Override
    public List<SharePasswordInfo> findByStorage(
            StorageIdentity storageIdentity) {
        // TODO: find by storage
        return List.of();
    }

    private UserShare findShareById(long shareId) {
        UserShare userShare =
                userShareRepository.getById(shareId);
        if (userShare == null) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_NOT_FOUND);
        }
        return userShare;
    }

    private UserShare findShareByShareCode(String shareCode) {
        UserShare userShare =
                userShareRepository.getByShareId(shareCode);
        if (userShare == null) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_NOT_FOUND);
        }
        return userShare;
    }

    @Override
    public ShareStructureInfo findStructureById(
            long shareId, long parentId) {
        UserShare userShare = findShareById(shareId);
        return getShareStructureInfo(userShare, parentId);
    }

    private ShareStructureInfo getShareStructureInfo(UserShare userShare, long parentId) {
        if (parentId < 0) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (parentId == UserFolder.ROOT) {
            return getByParentIfRoot(userShare);
        }
        if (userShare.getStorageType() != StorageType.FOLDER) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_NOT_FOUND);
        }
        FolderStructureInfo folderStructureInfo
                = userStorageSearchService.findFolder(parentId);
        // check share is in folder
        checkFolderInShare(userShare, folderStructureInfo);
        List<FolderInfo> parents = folderStructureInfo.getParents();

        List<? extends AttributedStorage> storages =
                userStorageSearchService.listFiles(parentId)
                        .stream()
                        .filter(storage -> !storage.isDeleted())
                        .toList();

        return ShareStructureInfo.of(
                userShare,
                parents,
                FolderInfo.of(folderStructureInfo),
                storages
        );
    }

    private void checkFolderInShare(UserShare userShare,
                                    FolderStructureInfo folderStructureInfo) {
        for (FolderInfo folderStructureInfoParent : folderStructureInfo.getParents()) {
            if (folderStructureInfoParent.getStorageId() == userShare.getStorageId()) {
                return;
            }
        }
        if (folderStructureInfo.getStorageId() == userShare.getStorageId()) {
            return;
        }
        throw new UserShareException(UserShareErrorCode.ERROR_SHARE_NOT_FOUND);
    }

    private ShareStructureInfo getByParentIfRoot(UserShare userShare) {
        AttributedStorage storage = userStorageSearchService.findStorage(new SimpleStorageIdentity(
                userShare.getStorageId(), userShare.getStorageType()));
        if (storage.isDeleted()) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        return ShareStructureInfo.of(
                userShare,
                List.of(),
                FolderInfo.ROOT,
                List.of(storage)
        );
    }

    @Override
    public ShareStructureInfo findStructureByShareCode(
            String shareCode, long parentId) {
        UserShare userShare = findShareByShareCode(shareCode);
        return getShareStructureInfo(userShare, parentId);
    }
}
