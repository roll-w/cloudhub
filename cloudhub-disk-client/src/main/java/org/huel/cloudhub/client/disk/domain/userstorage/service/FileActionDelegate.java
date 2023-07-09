package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;

/**
 * @author RollW
 */
public interface FileActionDelegate {
    Long createFile(UserFileStorage userFileStorage);

    void onDeleteFile(UserFileStorage userFileStorage);

    void updateFile(UserFileStorage userFileStorage);

    void checkExistsFile(String name, long parentId);

    AttributedStorage checkParentExists(long parentId);
}
