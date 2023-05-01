package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * @author RollW
 */
public interface UserDirectoryService {
    AttributedStorage createDirectory(String directoryName, long parentId,
                                      long owner, LegalUserType legalUserType)
            throws StorageException;

    void deleteDirectory(long directoryId, long owner, LegalUserType legalUserType);

    void renameDirectory(long directoryId, String newName,
                         long owner, LegalUserType legalUserType);

    void moveDirectory(long directoryId, long newParentId,
                       long owner, LegalUserType legalUserType);
}
