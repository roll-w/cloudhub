package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.BaseAbility;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceOperator;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.web.BusinessRuntimeException;

/**
 * 存储相关操作接口，自动在上下文记录操作日志。
 *
 * @author RollW
 */
@BaseAbility
public interface StorageAction extends AttributedStorage, SystemResourceOperator {
    StorageAction delete() throws StorageException;

    @Override
    StorageAction update() throws BusinessRuntimeException;

    void restore() throws StorageException;

    void create() throws StorageException;

    @Override
    StorageAction rename(String newName) throws StorageException;

    void move(long newParentId) throws StorageException;

    StorageAction copy(long newParentId) throws StorageException;

    @Override
    StorageAction getSystemResource();
}
