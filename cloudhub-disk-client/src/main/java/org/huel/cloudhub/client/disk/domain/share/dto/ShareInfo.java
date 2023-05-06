package org.huel.cloudhub.client.disk.domain.share.dto;

import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record ShareInfo(
        long id,
        long storageId,
        StorageType storageType,
        long creatorId,
        String shareCode,
        long expireTime,
        long createTime
) {

    public static ShareInfo from(UserShare userShare) {
        if (userShare == null) {
            return null;
        }

        return new ShareInfo(
                userShare.getId(),
                userShare.getStorageId(),
                userShare.getStorageType(),
                userShare.getUserId(),
                userShare.getShareId(),
                userShare.getExpireTime(),
                userShare.getCreateTime()
        );
    }

    public static ShareInfo from(SharePasswordInfo sharePasswordInfo) {
        if (sharePasswordInfo == null) {
            return null;
        }

        return new ShareInfo(
                sharePasswordInfo.id(),
                sharePasswordInfo.storageId(),
                sharePasswordInfo.storageType(),
                sharePasswordInfo.creatorId(),
                sharePasswordInfo.shareCode(),
                sharePasswordInfo.expireTime(),
                sharePasswordInfo.createTime()
        );
    }

}
