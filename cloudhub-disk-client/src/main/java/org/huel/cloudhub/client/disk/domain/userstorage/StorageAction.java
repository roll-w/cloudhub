package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;

/**
 * 存储相关操作接口，自动在上下文记录操作日志。
 *
 * @author RollW
 */
public interface StorageAction extends AttributedStorage {
    void delete() throws StorageException;

    void create() throws StorageException;

    void rename(String newName) throws StorageException;

    void move(long newParentId) throws StorageException;

    StorageAction copy(long newParentId) throws StorageException;
}
