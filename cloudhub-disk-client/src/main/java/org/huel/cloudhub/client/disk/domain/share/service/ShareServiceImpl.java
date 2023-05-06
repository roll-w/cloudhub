package org.huel.cloudhub.client.disk.domain.share.service;

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.RandomStringUtils;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.share.ShareService;
import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareErrorCode;
import org.huel.cloudhub.client.disk.domain.share.common.UserShareException;
import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.share.repository.UserShareRepository;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author RollW
 */
@Service
public class ShareServiceImpl implements ShareService {
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
                                   Duration time,
                                   Operator operator,
                                   String password) {
        validatePassword(password);

        StringBuilder sb = new StringBuilder();
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity);
        if (storage.isDeleted()) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        String hash = calcHash(storage);

        String random = RandomStringUtils.randomAlphanumeric(15);
        String shareId = sb.append(hash).append("_").append(random).toString();

        long expireTime = time.isNegative() ? 0 : time.toMillis();
        long now = System.currentTimeMillis();

        UserShare share = UserShare.builder()
                .setShareId(shareId)
                .setCreateTime(now)
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
        long hashCal = (ownerId) << (ownerId | (ordinal * 10));
        String hash = Hashing.goodFastHash(24).hashLong(hashCal).toString();
        String nameHash = Hashing.goodFastHash(24).hashString(
                        storage.getName() + storage.getStorageType(), StandardCharsets.UTF_8)
                .toString();
        return hash.substring(0, 8) +
                nameHash.substring(0, 8);
    }

    @Override
    public void cancelShare(long shareId) {
        // TODO: cancel share
    }
}
