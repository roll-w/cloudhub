package org.huel.cloudhub.client.disk.domain.share.vo;

import org.huel.cloudhub.client.disk.domain.share.dto.ShareStructureInfo;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;

import java.util.List;

/**
 * @author RollW
 */
public record ShareStructureVo(
        long id,
        long creatorId,
        String username,
        String nickname,
        String shareCode,
        boolean isPublic,
        long expireTime,
        long createTime,
        List<FolderInfo> parents,
        List<StorageVo> storages
) {

    public static ShareStructureVo from(ShareStructureInfo shareStructureInfo,
                                        AttributedUser attributedUser) {
        return new ShareStructureVo(
                shareStructureInfo.id(),
                shareStructureInfo.creatorId(),
                attributedUser.getUsername(),
                attributedUser.getNickname(),
                shareStructureInfo.shareCode(),
                shareStructureInfo.isPublic(),
                shareStructureInfo.expireTime(),
                shareStructureInfo.createTime(),
                shareStructureInfo.parents(),
                shareStructureInfo.storages()
                        .stream()
                        .map(StorageVo::from)
                        .toList()
        );
    }
}
