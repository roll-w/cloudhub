package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public record FolderStructureInfo(
        long storageId,
        String name,
        Long parentId,
        List<FolderInfo> parents,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long createTime,
        long updateTime,
        boolean deleted
) implements AttributedStorage {
    public static final FolderStructureInfo ROOT_FOLDER =
            FolderStructureInfo.of(UserFolder.ROOT_FOLDER, List.of());

    public List<FolderInfo> getParents() {
        return parents;
    }

    @Override
    public FileType getFileType() {
        return FileType.OTHER;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public long getStorageId() {
        return storageId;
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return StorageType.FOLDER;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public long getOwnerId() {
        return ownerId;
    }

    @NonNull
    @Override
    public LegalUserType getOwnerType() {
        return ownerType;
    }

    public static FolderStructureInfo of(UserFolder userFolder,
                                         List<FolderInfo> parents) {
        return new FolderStructureInfo(
                userFolder.getStorageId(),
                userFolder.getName(),
                userFolder.getParentId(),
                parents,
                userFolder.getOwnerId(),
                userFolder.getOwnerType(),
                userFolder.getCreateTime(),
                userFolder.getUpdateTime(),
                userFolder.isDeleted()
        );
    }
}
