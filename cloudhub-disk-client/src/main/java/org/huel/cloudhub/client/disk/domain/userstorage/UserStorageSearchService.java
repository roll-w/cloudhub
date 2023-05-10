package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.BaseAbility;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderStructureInfo;
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
    FolderStructureInfo findFolder(long folderId) throws StorageException;

    FolderStructureInfo findFolder(long folderId, StorageOwner storageOwner) throws StorageException;

    @NonNull
    FolderStructureInfo findFolder(FileStorageInfo fileStorageInfo) throws StorageException;

    @NonNull
    AttributedStorage findFile(long fileId) throws StorageException;

    AttributedStorage findFile(long fileId, StorageOwner storageOwner)
            throws StorageException;

    @NonNull
    AttributedStorage findFile(FileStorageInfo fileStorageInfo) throws StorageException;

    // include directories
    List<AttributedStorage> listFiles(long folderId, StorageOwner storageOwner);

    // don't care about the owner
    List<AttributedStorage> listFiles(long folderId);
}
