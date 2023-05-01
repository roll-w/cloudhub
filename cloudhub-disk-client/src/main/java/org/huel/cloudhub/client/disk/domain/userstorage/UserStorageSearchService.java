package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public interface UserStorageSearchService {
    @NonNull
    AttributedStorage findDirectory(long directoryId) throws StorageException;

    @NonNull
    AttributedStorage findDirectory(FileStorageInfo fileStorageInfo) throws StorageException;

    @NonNull
    AttributedStorage findFile(long fileId) throws StorageException;

    @NonNull
    AttributedStorage findFile(FileStorageInfo fileStorageInfo) throws StorageException;

    // include directories
    List<AttributedStorage> listFiles(long directoryId, StorageOwner storageOwner);
}
