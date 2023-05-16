package org.huel.cloudhub.client.disk.domain.share.vo;

import org.huel.cloudhub.client.disk.domain.share.dto.SharePasswordInfo;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

import java.util.Objects;

/**
 * @author RollW
 */
public record ShareInfoVo(
        long id,
        long storageId,
        StorageType storageType,
        long creatorId,
        String username,
        String nickname,
        String shareCode,
        boolean isPublic,
        long expireTime,
        long createTime
) {


    public static ShareInfoVo from(SharePasswordInfo sharePasswordInfo,
                                   AttributedUser attributedUser) {
        if (sharePasswordInfo == null) {
            return null;
        }

        return new ShareInfoVo(
                sharePasswordInfo.id(),
                sharePasswordInfo.storageId(),
                sharePasswordInfo.storageType(),
                sharePasswordInfo.creatorId(),
                attributedUser.getUsername(),
                Objects.requireNonNullElse(
                        attributedUser.getNickname(),
                        attributedUser.getUsername()
                ),
                sharePasswordInfo.shareCode(),
                sharePasswordInfo.isPublic(),
                sharePasswordInfo.expireTime(),
                sharePasswordInfo.createTime()
        );
    }

}
