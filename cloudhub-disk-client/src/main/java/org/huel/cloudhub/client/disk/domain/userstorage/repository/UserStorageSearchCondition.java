package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record UserStorageSearchCondition(
        StorageType storageType,
        StorageOwner storageOwner,
        String name, FileType fileType,
        Long parentId,
        Long before,
        Long after
) {
}
