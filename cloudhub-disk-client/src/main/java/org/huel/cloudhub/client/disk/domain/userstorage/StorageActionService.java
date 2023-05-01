package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * @author RollW
 */
public interface StorageActionService {
    StorageAction openStorageAction(Storage storage);

    StorageAction openStorageAction(long storageId,
                                    StorageType storageType)
            throws StorageException;

    StorageAction openStorageAction(long storageId, StorageType storageType,
                                    long ownerId, LegalUserType userType)
            throws StorageException;
}
