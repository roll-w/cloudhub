package org.huel.cloudhub.client.disk.domain.userstorage.dto;

import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

/**
 * @author RollW
 */
public record FileStorageInfo(
        String fileName,
        long directoryId,
        StorageOwner storageOwner
) {
}
