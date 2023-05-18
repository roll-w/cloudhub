package org.huel.cloudhub.client.disk.domain.share.dto;

import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderInfo;

import java.util.List;

/**
 * @author RollW
 */
public record ShareStructureInfo(
        long id,
        long creatorId,
        String shareCode,
        String password,
        boolean isPublic,
        long expireTime,
        long createTime,
        List<FolderInfo> parents,
        FolderInfo current,
        List<? extends AttributedStorage> storages
) {

    public boolean isExpired(long time) {
        if (expireTime <= 0) {
            return false;
        }
        return time > expireTime;
    }

    public static ShareStructureInfo of(UserShare userShare,
                                        List<FolderInfo> parents,
                                        FolderInfo current,
                                        List<? extends AttributedStorage> storages) {
        return new ShareStructureInfo(
                userShare.getId(),
                userShare.getUserId(),
                userShare.getShareId(),
                userShare.getPassword(),
                userShare.isPublic(),
                userShare.getExpireTime(),
                userShare.getCreateTime(),
                parents,
                current,
                storages
        );
    }
}
