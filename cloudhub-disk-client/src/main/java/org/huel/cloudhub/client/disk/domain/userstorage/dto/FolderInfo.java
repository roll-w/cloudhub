package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record FolderInfo(
        long id,
        String name,
        long parentId,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long createTime,
        long updateTime,
        boolean deleted
) implements Storage {
    @Override
    public long getStorageId() {
        return id();
    }

    @NonNull
    @Override
    public StorageType getStorageType() {
        return StorageType.FOLDER;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Long getParentId() {
        return parentId();
    }

    @Override
    public long getOwnerId() {
        return ownerId();
    }

    @NonNull
    @Override
    public LegalUserType getOwnerType() {
        return ownerType();
    }

    public static FolderInfo of(UserFolder userFolder) {
        return new FolderInfo(
                userFolder.getId(),
                userFolder.getName(),
                userFolder.getParentId(),
                userFolder.getOwnerId(),
                userFolder.getOwnerType(),
                userFolder.getCreateTime(),
                userFolder.getUpdateTime(),
                userFolder.isDeleted()
        );
    }
}
