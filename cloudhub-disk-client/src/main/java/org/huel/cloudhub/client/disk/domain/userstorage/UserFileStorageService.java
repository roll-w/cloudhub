package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
public interface UserFileStorageService extends UserDirectoryService {
    AttributedStorage createDirectory(String directoryName, long parentId,
                                      long owner, LegalUserType legalUserType)
            throws StorageException;

    AttributedStorage uploadFile(FileStorageInfo fileStorageInfo,
                                 FileStreamInfo fileStreamInfo) throws IOException;

    void downloadFile(long fileId, long owner, LegalUserType legalUserType,
                      OutputStream outputStream)
            throws IOException, StorageException;

    void downloadFile(FileStorageInfo fileStorageInfo, OutputStream outputStream)
            throws IOException, StorageException;

    void deleteFile(long fileId, long owner, LegalUserType legalUserType) throws StorageException;

    void deleteFile(FileStorageInfo fileStorageInfo) throws StorageException;
}
