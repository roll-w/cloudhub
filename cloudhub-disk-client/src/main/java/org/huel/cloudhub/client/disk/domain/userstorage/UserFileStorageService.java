package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
public interface UserFileStorageService extends UserDirectoryService {
    AttributedStorage uploadFile(FileStorageInfo fileStorageInfo,
                                 FileStreamInfo fileStreamInfo) throws IOException;

    void downloadFile(long fileId, StorageOwner storageOwner,
                      OutputStream outputStream)
            throws IOException, StorageException;

    void downloadFile(FileStorageInfo fileStorageInfo, OutputStream outputStream)
            throws IOException, StorageException;

    void deleteFile(long fileId, StorageOwner storageOwner) throws StorageException;

    void deleteFile(FileStorageInfo fileStorageInfo) throws StorageException;
}
