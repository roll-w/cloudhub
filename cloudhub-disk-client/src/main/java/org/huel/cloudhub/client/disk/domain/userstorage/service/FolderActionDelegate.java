package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;

/**
 * @author RollW
 */
public interface FolderActionDelegate {
    Long createDirectory(UserFolder userFolder);

    void updateDirectory(UserFolder userFolder);

    void checkExistsFolder(String name, long parentId);

    AttributedStorage checkParentExists(long parentId);

    boolean checkFolderHasActiveChildren(long id);
}
