package org.huel.cloudhub.client.disk.domain.versioned.vo;

import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;

/**
 * @author RollW
 */
public record VersionedStorageVo(
        long id,
        long version,
        long storageId,
        StorageType storageType,
        long operatorId,
        String username,
        long createTime
) {

    public static VersionedStorageVo of(VersionedFileStorage versionedFileStorage) {
        if (versionedFileStorage == null) {
            return null;
        }
        return new VersionedStorageVo(
                versionedFileStorage.getId(),
                versionedFileStorage.getVersion(),
                versionedFileStorage.getStorageId(),
                versionedFileStorage.getStorageType(),
                versionedFileStorage.getOperator(),
                null,
                versionedFileStorage.getCreateTime()
        );
    }

    public static VersionedStorageVo of(VersionedFileStorage versionedFileStorage,
                                        AttributedUser attributedUser) {
        if (versionedFileStorage == null) {
            return null;
        }
        return new VersionedStorageVo(
                versionedFileStorage.getId(),
                versionedFileStorage.getVersion(),
                versionedFileStorage.getStorageId(),
                versionedFileStorage.getStorageType(),
                versionedFileStorage.getOperator(),
                attributedUser.getUsername(),
                versionedFileStorage.getCreateTime()
        );
    }
}
