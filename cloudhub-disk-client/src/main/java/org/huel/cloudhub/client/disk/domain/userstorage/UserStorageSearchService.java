package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;

import java.util.List;

/**
 * @author RollW
 */
public interface UserStorageSearchService {
    UserDirectory findDirectory(long directoryId) throws StorageException;

    UserDirectory findDirectory(FileStorageInfo fileStorageInfo) throws StorageException;

    List<UserDirectory> listDirectories(long directoryId, StorageOwner storageOwner);

    UserFileStorage findFile(long fileId) throws StorageException;

    UserFileStorage findFile(FileStorageInfo fileStorageInfo) throws StorageException;
}
