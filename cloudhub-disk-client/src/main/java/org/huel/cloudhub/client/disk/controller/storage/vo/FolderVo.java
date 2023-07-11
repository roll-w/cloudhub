package org.huel.cloudhub.client.disk.controller.storage.vo;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderStructureInfo;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public record FolderVo(
        long storageId,
        String name,
        Long parentId,
        List<FolderInfo> parents,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long createTime,
        long updateTime,
        boolean deleted
) {

    public static FolderVo of(UserFolder folder, List<FolderInfo> parents) {
        return new FolderVo(
                folder.getStorageId(),
                folder.getName(),
                folder.getParentId(),
                parents,
                folder.getOwnerId(),
                folder.getOwnerType(),
                folder.getCreateTime(),
                folder.getUpdateTime(),
                folder.isDeleted()
        );
    }

    public static FolderVo of(FolderStructureInfo structureInfo) {
        return new FolderVo(
                structureInfo.getStorageId(),
                structureInfo.getName(),
                structureInfo.getParentId(),
                structureInfo.getParents(),
                structureInfo.getOwnerId(),
                structureInfo.getOwnerType(),
                structureInfo.getCreateTime(),
                structureInfo.getUpdateTime(),
                structureInfo.isDeleted()
        );
    }

}
