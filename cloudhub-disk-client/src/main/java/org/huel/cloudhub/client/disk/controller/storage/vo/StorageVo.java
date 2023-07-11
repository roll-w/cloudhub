package org.huel.cloudhub.client.disk.controller.storage.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
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
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long size,
        long createTime,
        long updateTime,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String fileId
) {
    public static StorageVo from(AttributedStorage storage) {
        return from(storage, null, null);
    }

    public static StorageVo from(AttributedStorage storage, Long size) {
        return from(storage, size, null);
    }

    public static StorageVo from(AttributedStorage storage,
                                 Long size,
                                 String fileId) {
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
                storage.getUpdateTime(),
                fileId
        );
    }
}
