package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class FolderAction implements StorageAction {
    private final UserFolder.Builder folderBuilder;
    private final FolderActionDelegate folderActionDelegate;

    private UserFolder folder;

    public FolderAction(UserFolder folder,
                        FolderActionDelegate folderActionDelegate) {
        this.folderBuilder = folder.toBuilder();
        this.folder = folder;
        this.folderActionDelegate = folderActionDelegate;
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
    public void delete() throws StorageException {
        if (folder.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_ALREADY_DELETED);
        }

        folderBuilder.setDeleted(true);
        update();
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
    public void rename(String newName) throws StorageException {
        folderActionDelegate.checkExistsFolder(newName, folder.getParentId());

        folderBuilder.setName(newName);
        OperationContextHolder.getContext()
                .setOriginContent(folder.getName())
                .setChangedContent(newName);
        update();
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
        folderBuilder.setParentId(newParentId);
        OperationContextHolder.getContext()
                .setOriginContent(parent.getName())
                .setChangedContent(storage.getName());
        update();
    }

    @Override
    public StorageAction copy(long newParentId)
            throws StorageException {
        if (folder.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
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

    private void update() {
        UserFolder updatedDirectory = folderBuilder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        folderActionDelegate.updateDirectory(updatedDirectory);
        folder = updatedDirectory;
        OperationContextHolder.getContext()
                .addSystemResource(updatedDirectory);
    }
}
