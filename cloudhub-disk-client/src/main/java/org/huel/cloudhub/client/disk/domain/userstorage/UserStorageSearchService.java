package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.BaseAbility;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@BaseAbility
public interface UserStorageSearchService {
    AttributedStorage findStorage(StorageIdentity storageIdentity) throws StorageException;

    AttributedStorage findStorage(StorageIdentity storageIdentity,
                                  StorageOwner storageOwner) throws StorageException;

    @NonNull
    AttributedStorage findDirectory(long directoryId) throws StorageException;

    AttributedStorage findDirectory(long directoryId, StorageOwner storageOwner) throws StorageException;

    @NonNull
    AttributedStorage findDirectory(FileStorageInfo fileStorageInfo) throws StorageException;

    @NonNull
    AttributedStorage findFile(long fileId) throws StorageException;

    AttributedStorage findFile(long fileId, StorageOwner storageOwner)
            throws StorageException;

    @NonNull
    AttributedStorage findFile(FileStorageInfo fileStorageInfo) throws StorageException;

    // include directories
    List<AttributedStorage> listFiles(long directoryId, StorageOwner storageOwner);

    // don't care about the owner
    List<AttributedStorage> listFiles(long directoryId);
}
