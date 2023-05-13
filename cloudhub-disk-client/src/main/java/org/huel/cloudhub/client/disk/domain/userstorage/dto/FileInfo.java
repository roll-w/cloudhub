package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public record FileInfo(
        long storageId,
        String name,
        long ownerId,
        @NonNull LegalUserType ownerType,
        long parentId,
        String fileId,
        FileType fileType,
        String mimeType,
        long createTime,
        long updateTime,
        boolean deleted
) implements AttributedStorage {
    @Override
    public FileType getFileType() {
        return fileType;
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
        return StorageType.FILE;
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

    public String getFileId() {
        return fileId;
    }

    public static FileInfo from(UserFileStorage userFileStorage) {
        return new FileInfo(
                userFileStorage.getStorageId(),
                userFileStorage.getName(),
                userFileStorage.getOwnerId(),
                userFileStorage.getOwnerType(),
                userFileStorage.getParentId(),
                userFileStorage.getFileId(),
                userFileStorage.getFileType(),
                userFileStorage.getMimeType(),
                userFileStorage.getCreateTime(),
                userFileStorage.getUpdateTime(),
                userFileStorage.isDeleted()
        );
    }

}
