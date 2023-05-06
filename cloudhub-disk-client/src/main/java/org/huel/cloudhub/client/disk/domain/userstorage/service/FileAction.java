package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class FileAction implements StorageAction {
    private final UserFileStorage.Builder fileBuilder;
    private final FileActionDelegate fileActionDelegate;
    private UserFileStorage file;


    public FileAction(UserFileStorage file, FileActionDelegate fileActionDelegate) {
        this.fileBuilder = file.toBuilder();
        this.fileActionDelegate = fileActionDelegate;
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public long getStorageId() {
        return file.getStorageId();
    }

    @Override
    @NonNull
    public StorageType getStorageType() {
        return file.getStorageType();
    }

    @Override
    public Long getParentId() {
        return file.getParentId();
    }

    @Override
    public long getOwnerId() {
        return file.getOwnerId();
    }

    @Override
    @NonNull
    public LegalUserType getOwnerType() {
        return file.getOwnerType();
    }

    @Override
    public long getCreateTime() {
        return file.getCreateTime();
    }

    @Override
    public long getUpdateTime() {
        return file.getUpdateTime();
    }

    @Override
    public boolean isDeleted() {
        return file.isDeleted();
    }

    @Override
    public void delete() throws StorageException {
        if (file.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_ALREADY_DELETED);
        }

        fileBuilder.setDeleted(true);
        update();
    }

    @Override
    public void restore() throws StorageException {
        if (!file.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_DELETED);
        }

        fileBuilder.setDeleted(false);
        update();
    }

    @Override
    public void create() throws StorageException {
        insert();
    }

    @Override
    public void rename(String newName) throws StorageException {
        fileBuilder.setName(newName);
        update();
    }

    @Override
    public void move(long newParentId) throws StorageException {
        if (file.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        fileBuilder.setDirectoryId(newParentId);
        update();
    }

    @Override
    public StorageAction copy(long newParentId) throws StorageException {
        if (file.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        UserFileStorage copiedFile = fileBuilder
                .setDirectoryId(newParentId)
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .build();
        Long id = fileActionDelegate.createFile(copiedFile);
        copiedFile = copiedFile.toBuilder()
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(copiedFile)
                .setChangedContent(copiedFile.getName());
        return new FileAction(copiedFile, fileActionDelegate);
    }

    private void insert() {
        UserFileStorage insertedFile = fileBuilder
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .build();
        if (insertedFile.getId() != null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_EXISTED);
        }

        Long id = fileActionDelegate.createFile(insertedFile);
        file = fileBuilder
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(file)
                .setChangedContent(file.getName());
    }

    private void update() {
        UserFileStorage updatedFile = fileBuilder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        fileActionDelegate.updateFile(updatedFile);
        file = updatedFile;
        OperationContextHolder.getContext()
                .addSystemResource(file)
                .setChangedContent(file.getName());
    }
}
