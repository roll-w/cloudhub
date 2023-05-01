package org.huel.cloudhub.client.disk.domain.userstorage.vo;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record StorageVo(
        long storageId,
        String name,
        StorageType storageType,
        long ownerId,
        LegalUserType legalUserType,
        Long parentId,
        long size,
        long createTime,
        long updateTime
) {
    public static StorageVo from(Storage storage, long size) {
        if (!(storage instanceof AttributedStorage attributedStorage)) {
            return new StorageVo(
                    storage.getStorageId(),
                    storage.getName(),
                    storage.getStorageType(),
                    storage.getOwnerId(),
                    storage.getOwnerType(),
                    storage.getParentId(),
                    size,
                    0,
                    0
            );
        }

        return new StorageVo(
                storage.getStorageId(),
                storage.getName(),
                storage.getStorageType(),
                storage.getOwnerId(),
                storage.getOwnerType(),
                storage.getParentId(),
                size,
                attributedStorage.getCreateTime(),
                attributedStorage.getUpdateTime()
        );
    }
}
