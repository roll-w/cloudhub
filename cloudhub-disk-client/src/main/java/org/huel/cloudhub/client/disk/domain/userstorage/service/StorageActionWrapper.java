package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageEventListener;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.web.BusinessRuntimeException;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class StorageActionWrapper implements StorageAction {
    private final StorageAction wrapped;
    private final StorageEventListener storageEventListener;

    public StorageActionWrapper(StorageAction storageAction,
                                StorageEventListener storageEventListener) {
        this.wrapped = storageAction;
        this.storageEventListener = storageEventListener;
    }

    public StorageAction getWrapped() {
        return wrapped;
    }

    @Override
    public long getStorageId() {
        return wrapped.getStorageId();
    }

    @Override
    @NonNull
    public StorageType getStorageType() {
        return wrapped.getStorageType();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Long getParentId() {
        return wrapped.getParentId();
    }

    @Override
    public long getOwnerId() {
        return wrapped.getOwnerId();
    }

    @Override
    @NonNull
    public LegalUserType getOwnerType() {
        return wrapped.getOwnerType();
    }

    @Override
    public FileType getFileType() {
        return wrapped.getFileType();
    }

    @Override
    public long getCreateTime() {
        return wrapped.getCreateTime();
    }

    @Override
    public long getUpdateTime() {
        return wrapped.getUpdateTime();
    }

    @Override
    public boolean isDeleted() {
        return wrapped.isDeleted();
    }

    @Override
    public StorageAction update() throws BusinessRuntimeException {
        return wrapped.update();
    }

    @Override
    public StorageAction delete() throws StorageException {
        return wrapped.delete();
    }

    @Override
    public void restore() throws StorageException {
        wrapped.restore();
    }

    @Override
    public void create() throws StorageException {
        wrapped.create();
    }

    @Override
    public StorageAction rename(String newName) throws StorageException {
        return wrapped.rename(newName);
    }

    @Override
    public StorageAction getSystemResource() {
        return wrapped.getSystemResource();
    }

    @Override
    public void move(long newParentId) throws StorageException {
        wrapped.move(newParentId);
    }

    @Override
    public StorageAction copy(long newParentId) throws StorageException {
        return wrapped.copy(newParentId);
    }

    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        wrapped.setCheckDeleted(checkDeleted);
    }

    @Override
    public boolean isCheckDeleted() {
        return wrapped.isCheckDeleted();
    }
}
