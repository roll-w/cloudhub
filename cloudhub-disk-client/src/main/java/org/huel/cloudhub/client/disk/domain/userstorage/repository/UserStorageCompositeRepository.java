package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;

import java.util.List;

/**
 * @author RollW
 */
public interface UserStorageCompositeRepository {
    List<AttributedStorage> listStorages();

    List<AttributedStorage> listStorages(StorageOwner storageOwner);
}
