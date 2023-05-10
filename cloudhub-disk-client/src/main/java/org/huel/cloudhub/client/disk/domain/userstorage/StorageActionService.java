package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.BaseAbility;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * @author RollW
 */
@BaseAbility
public interface StorageActionService {
    StorageAction openStorageAction(StorageIdentity storage)
            throws StorageException;

    StorageAction openStorageAction(StorageIdentity storageIdentity,
                                    StorageOwner storageOwner)
            throws StorageException;
}
