package org.huel.cloudhub.client.disk.domain.userstorage;

import java.util.List;

/**
 * @author RollW
 */
public interface FileRecycleService {
    List<AttributedStorage> listRecycle(StorageOwner storageOwner);

    void revertRecycle(StorageIdentity storageIdentity, StorageOwner storageOwner);
}
