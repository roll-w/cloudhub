package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.web.BusinessRuntimeException;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class FileAction implements StorageAction {
    private final UserFileStorage.Builder fileBuilder;
    private final FileActionDelegate fileActionDelegate;
    private UserFileStorage file;
    private boolean checkDelete;


    public FileAction(UserFileStorage file,
                      FileActionDelegate fileActionDelegate) {
        this(file, fileActionDelegate, false);
    }

    public FileAction(UserFileStorage file,
                      FileActionDelegate fileActionDelegate,
                      boolean checkDelete) {
        this.fileBuilder = file.toBuilder();
        this.fileActionDelegate = fileActionDelegate;
        this.file = file;
        this.checkDelete = checkDelete;
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
    public FileType getFileType() {
        return file.getFileType();
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
    public StorageAction delete() throws StorageException {
        if (file.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_ALREADY_DELETED);
        }

        fileBuilder.setDeleted(true);
        OperationContextHolder.getContext()
                .setOriginContent(file.getName());
        return updateInternal();
    }

    @Override
    public void restore() throws StorageException {
        if (!file.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_DELETED);
        }

        fileBuilder.setDeleted(false);
        OperationContextHolder.getContext()
                .setChangedContent(file.getName());
        updateInternal();
    }

    @Override
    public void create() throws StorageException {
        insert();
    }

    @Override
    public StorageAction rename(String newName) throws StorageException {
        checkDeleted();
        fileActionDelegate.checkExistsFile(newName, file.getParentId());
        fileBuilder.setName(newName);
        OperationContextHolder.getContext()
                .setOriginContent(file.getName())
                .setChangedContent(newName);
        return updateInternal();
    }

    @Override
    public StorageAction getSystemResource() {
        return this;
    }

    @Override
    public void move(long newParentId) throws StorageException {
        checkDeleted();
        if (file.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        AttributedStorage storage =
                fileActionDelegate.checkParentExists(newParentId);
        AttributedStorage parent =
                fileActionDelegate.checkParentExists(file.getParentId());
        fileActionDelegate.checkExistsFile(file.getName(), newParentId);

        fileBuilder.setFolderId(newParentId);
        OperationContextHolder.getContext()
                .setOriginContent(parent.getName())
                .setChangedContent(storage.getName());
        updateInternal();
    }

    @Override
    public StorageAction copy(long newParentId) throws StorageException {
        checkDeleted();
        if (file.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        fileActionDelegate.checkParentExists(newParentId);
        fileActionDelegate.checkExistsFile(file.getName(), newParentId);
        long time = System.currentTimeMillis();
        UserFileStorage.Builder copiedBuilder = file.toBuilder()
                .setId(null)
                .setFolderId(newParentId).setCreateTime(time)
                .setUpdateTime(time);
        // TODO: add storage create event callback
        UserFileStorage copiedFile = copiedBuilder.build();
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

    @Override
    public StorageAction update() throws BusinessRuntimeException {
        return this;
    }

    private StorageAction updateInternal() {
        UserFileStorage updatedFile = fileBuilder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        fileActionDelegate.updateFile(updatedFile);
        file = updatedFile;
        OperationContextHolder.getContext()
                .addSystemResource(file);
        return this;
    }

    @Override
    public void setCheckDeleted(boolean checkDeleted) {
        this.checkDelete = checkDeleted;
    }

    @Override
    public boolean isCheckDeleted() {
        return checkDelete;
    }

    private void checkDeleted() throws StorageException {
        if (!checkDelete) {
            return;
        }
        if (file.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_ALREADY_DELETED);
        }
    }
}
