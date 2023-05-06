package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * @author RollW
 */
public interface UserDirectoryService {
    AttributedStorage createDirectory(String directoryName, long parentId,
                                      StorageOwner storageOwner)
            throws StorageException;
}
