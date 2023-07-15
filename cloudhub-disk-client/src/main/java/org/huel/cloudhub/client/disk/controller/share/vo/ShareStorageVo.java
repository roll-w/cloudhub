package org.huel.cloudhub.client.disk.controller.share.vo;

import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.controller.storage.vo.StorageVo;

/**
 * @author RollW
 */
public record ShareStorageVo(
        long id,
        long creatorId,
        String shareCode,
        boolean isPublic,
        long expireTime,
        long createTime,
        String password,
        StorageVo storage
) {

    public static ShareStorageVo from(UserShare userShare,
                                      AttributedStorage storage) {
        return new ShareStorageVo(
                userShare.getId(),
                userShare.getStorageId(),
                userShare.getShareId(),
                userShare.isPublic(),
                userShare.getExpireTime(),
                userShare.getCreateTime(),
                userShare.getPassword(),
                StorageVo.from(storage)
        );
    }

    public static ShareStorageVo from(SharePasswordInfo sharePasswordInfo,
                                      AttributedStorage storage) {
        return new ShareStorageVo(
                sharePasswordInfo.id(),
                sharePasswordInfo.creatorId(),
                sharePasswordInfo.shareCode(),
                sharePasswordInfo.isPublic(),
                sharePasswordInfo.expireTime(),
                sharePasswordInfo.createTime(),
                sharePasswordInfo.password(),
                StorageVo.from(storage)
        );
    }
}
