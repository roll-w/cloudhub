package org.huel.cloudhub.client.disk.domain.share.dto;

import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record SharePasswordInfo(
        long id,
        long storageId,
        StorageType storageType,
        long creatorId,
        String shareCode,
        // as a utility field, can be removed if not needed
        boolean isPublic,
        long expireTime,
        long createTime,
        String password
) {

    public boolean isExpired(long time) {
        if (expireTime <= 0) {
            return false;
        }
        return time > expireTime;
    }

    public static SharePasswordInfo from(ShareInfo shareInfo, String password) {
        if (shareInfo == null) {
            return null;
        }

        return new SharePasswordInfo(
                shareInfo.id(),
                shareInfo.storageId(),
                shareInfo.storageType(),
                shareInfo.creatorId(),
                shareInfo.shareCode(),
                shareInfo.isPublic(),
                shareInfo.expireTime(),
                shareInfo.createTime(),
                password
        );
    }

    public static SharePasswordInfo from(UserShare userShare) {
        if (userShare == null) {
            return null;
        }

        return new SharePasswordInfo(
                userShare.getId(),
                userShare.getStorageId(),
                userShare.getStorageType(),
                userShare.getUserId(),
                userShare.getShareId(),
                userShare.isPublic(),
                userShare.getExpireTime(),
                userShare.getCreateTime(),
                userShare.getPassword()
        );
    }
}
