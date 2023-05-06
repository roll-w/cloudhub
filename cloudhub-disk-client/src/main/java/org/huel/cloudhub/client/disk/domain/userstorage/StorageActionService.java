package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.BaseAbility;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * @author RollW
 */
@BaseAbility
public interface StorageActionService {
    StorageAction openStorageAction(Storage storage);

    StorageAction openStorageAction(long storageId,
                                    StorageType storageType)
            throws StorageException;

    StorageAction openStorageAction(long storageId, StorageType storageType,
                                    StorageOwner storageOwner)
            throws StorageException;
}
