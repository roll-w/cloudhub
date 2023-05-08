package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * @author RollW
 */
public interface UserFolderService {
    AttributedStorage createFolder(String folderName, long parentId,
                                   StorageOwner storageOwner)
            throws StorageException;
}
