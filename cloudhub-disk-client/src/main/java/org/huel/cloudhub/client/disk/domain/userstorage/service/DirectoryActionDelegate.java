package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;

/**
 * @author RollW
 */
public interface DirectoryActionDelegate {
    Long createDirectory(UserDirectory userDirectory);

    void updateDirectory(UserDirectory userDirectory);
}
