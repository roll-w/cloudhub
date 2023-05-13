package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
public interface UserFileStorageService extends UserFolderService {
    AttributedStorage uploadFile(FileStorageInfo fileStorageInfo,
                                 FileStreamInfo fileStreamInfo) throws IOException;

    void downloadFile(long fileId, StorageOwner storageOwner,
                      OutputStream outputStream)
            throws IOException, StorageException;

    void downloadFile(long fileId, OutputStream outputStream)
            throws IOException;

    void downloadFile(long fileId, OutputStream outputStream,
                      long startBytes, long endBytes)
            throws IOException;

    void downloadFile(FileInfo fileInfo, OutputStream outputStream)
            throws IOException;

    void downloadFile(FileInfo fileInfo, OutputStream outputStream,
                      long startBytes, long endBytes)
            throws IOException;

    void downloadFile(FileStorageInfo fileStorageInfo, OutputStream outputStream)
            throws IOException, StorageException;

}
