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
public class FolderAction implements StorageAction {
    private final UserFolder.Builder folderBuilder;
    private final FolderActionDelegate folderActionDelegate;

    private UserFolder folder;
    private boolean checkDelete;

    public FolderAction(UserFolder folder,
                        FolderActionDelegate folderActionDelegate) {
        this(folder, folderActionDelegate, false);
    }

    public FolderAction(UserFolder folder,
                        FolderActionDelegate folderActionDelegate,
                        boolean checkDelete) {
        this.folderBuilder = folder.toBuilder();
        this.folder = folder;
        this.folderActionDelegate = folderActionDelegate;
        this.checkDelete = checkDelete;
    }

    @Override
    public long getStorageId() {
        return folder.getStorageId();
    }

    @Override
    @NonNull
    public StorageType getStorageType() {
        return folder.getStorageType();
    }

    @Override
    public Long getParentId() {
        return folder.getParentId();
    }

    @Override
    public long getOwnerId() {
        return folder.getOwnerId();
    }

    @Override
    @NonNull
    public LegalUserType getOwnerType() {
        return folder.getOwnerType();
    }

    @Override
    public String getName() {
        return folder.getName();
    }

    @Override
    public FileType getFileType() {
        return FileType.OTHER;
    }

    @Override
    public long getCreateTime() {
        return folder.getCreateTime();
    }

    @Override
    public long getUpdateTime() {
        return folder.getUpdateTime();
    }

    @Override
    public boolean isDeleted() {
        return folder.isDeleted();
    }

    @Override
    public StorageAction update() throws BusinessRuntimeException {
        return this;
    }

    @Override
    public StorageAction delete() throws StorageException {
        if (folder.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_ALREADY_DELETED);
        }
        if (folderActionDelegate.checkFolderHasActiveChildren(folder.getId())) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EMPTY);
        }
        folderBuilder.setDeleted(true);
        OperationContextHolder.getContext()
                .setOriginContent(folder.getName());
        return updateInternal();
    }

    @Override
    public void restore() throws StorageException {
        // not support restore of directory
    }

    @Override
    public void create() throws StorageException {
        insert();
    }

    @Override
    public StorageAction rename(String newName) throws StorageException {
        folderActionDelegate.checkExistsFolder(newName, folder.getParentId());

        folderBuilder.setName(newName);
        OperationContextHolder.getContext()
                .setOriginContent(folder.getName())
                .setChangedContent(newName);
        return updateInternal();
    }

    @Override
    public StorageAction getSystemResource() {
        return this;
    }

    @Override
    public void move(long newParentId) throws StorageException {
        if (folder.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        AttributedStorage storage =
                folderActionDelegate.checkParentExists(newParentId);
        AttributedStorage parent =
                folderActionDelegate.checkParentExists(folder.getParentId());
        folderActionDelegate.checkExistsFolder(folder.getName(), newParentId);

        folderBuilder.setParentId(newParentId);
        OperationContextHolder.getContext()
                .setOriginContent(parent.getName())
                .setChangedContent(storage.getName());
        updateInternal();
    }

    @Override
    public StorageAction copy(long newParentId)
            throws StorageException {
        if (folder.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        AttributedStorage storage =
                folderActionDelegate.checkParentExists(newParentId);
        folderActionDelegate.checkExistsFolder(folder.getName(), newParentId);

        UserFolder copy = folderBuilder.build().toBuilder()
                .setId(null)
                .setParentId(newParentId)
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .build();
        Long id = folderActionDelegate.createDirectory(copy);
        copy = copy.toBuilder()
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(copy)
                .setChangedContent(copy.getName());
        return new FolderAction(copy, folderActionDelegate);
    }

    private void insert() {
        UserFolder insertedDirectory = folderBuilder
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .build();
        if (insertedDirectory.getId() != null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_EXISTED);
        }

        Long id = folderActionDelegate.createDirectory(insertedDirectory);
        folder = folderBuilder
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(folder)
                .setChangedContent(folder.getName());
    }

    private StorageAction updateInternal() {
        UserFolder updatedDirectory = folderBuilder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        folderActionDelegate.updateDirectory(updatedDirectory);
        folder = updatedDirectory;
        OperationContextHolder.getContext()
                .addSystemResource(updatedDirectory);
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
}
