package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RollW
 */
public interface UserFileStorageService {
    Storage createDirectory(String directoryName,
                         long parentId, long owner, LegalUserType legalUserType);

    Storage uploadFile(FileStorageInfo fileStorageInfo,
                    FileStreamInfo fileStreamInfo) throws IOException;

    void downloadFile(long fileId, long owner, LegalUserType legalUserType,
                      OutputStream outputStream)
            throws IOException;

    void downloadFile(FileStorageInfo fileStorageInfo, OutputStream outputStream)
            throws IOException;

    void deleteFile(long fileId, long owner, LegalUserType legalUserType);

    void deleteFile(FileStorageInfo fileStorageInfo);
}
