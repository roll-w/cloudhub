package org.huel.cloudhub.client.disk.domain.userstorage.vo;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record StorageVo(
        long storageId,
        String name,
        StorageType storageType,
        long ownerId,
        LegalUserType ownerType,
        Long parentId,
        FileType fileType,
        long size,
        long createTime,
        long updateTime
) {
    public static StorageVo from(AttributedStorage storage) {
        return from(storage, 0);
    }

    public static StorageVo from(AttributedStorage storage, long size) {
        if (storage == null) {
            return null;
        }

        return new StorageVo(
                storage.getStorageId(),
                storage.getName(),
                storage.getStorageType(),
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getParentId(),
                storage.getFileType(),
                size,
                storage.getCreateTime(),
                storage.getUpdateTime()
        );
    }
}
