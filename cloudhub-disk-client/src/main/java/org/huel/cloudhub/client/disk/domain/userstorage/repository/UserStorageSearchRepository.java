package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;

import java.util.List;

/**
 * @author RollW
 */
public interface UserStorageSearchRepository {
    List<? extends AttributedStorage> findStoragesBy(UserStorageSearchCondition userStorageSearchCondition);
}
