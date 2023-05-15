package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
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
public class DirectoryAction implements StorageAction {
    private final UserFolder.Builder directoryBuilder;
    private final DirectoryActionDelegate directoryActionDelegate;

    private UserFolder directory;

    public DirectoryAction(UserFolder directory,
                           DirectoryActionDelegate directoryActionDelegate) {
        this.directoryBuilder = directory.toBuilder();
        this.directory = directory;
        this.directoryActionDelegate = directoryActionDelegate;
    }

    @Override
    public long getStorageId() {
        return directory.getStorageId();
    }

    @Override
    @NonNull
    public StorageType getStorageType() {
        return directory.getStorageType();
    }

    @Override
    public Long getParentId() {
        return directory.getParentId();
    }

    @Override
    public long getOwnerId() {
        return directory.getOwnerId();
    }

    @Override
    @NonNull
    public LegalUserType getOwnerType() {
        return directory.getOwnerType();
    }

    @Override
    public String getName() {
        return directory.getName();
    }

    @Override
    public FileType getFileType() {
        return FileType.OTHER;
    }

    @Override
    public long getCreateTime() {
        return directory.getCreateTime();
    }

    @Override
    public long getUpdateTime() {
        return directory.getUpdateTime();
    }

    @Override
    public boolean isDeleted() {
        return directory.isDeleted();
    }

    @Override
    public void delete() throws StorageException {
        if (directory.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_ALREADY_DELETED);
        }

        directoryBuilder.setDeleted(true);
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
        directoryActionDelegate.checkExistsFolder(newName, directory.getParentId());

        directoryBuilder.setName(newName);
        OperationContextHolder.getContext()
                .setOriginContent(directory.getName())
                .setChangedContent(newName);
        update();
    }

    @Override
    public void move(long newParentId) throws StorageException {
        if (directory.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        // TODO: check new parent directory is exist
        directoryBuilder.setParentId(newParentId);
        update();
    }

    @Override
    public StorageAction copy(long newParentId)
            throws StorageException {
        if (directory.getParentId() == newParentId) {
            throw new StorageException(StorageErrorCode.ERROR_SAME_DIRECTORY);
        }
        UserFolder copy = directoryBuilder.build().toBuilder()
                .setId(null)
                .setParentId(newParentId)
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .build();
        Long id = directoryActionDelegate.createDirectory(copy);
        copy = copy.toBuilder()
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(copy)
                .setChangedContent(copy.getName());
        return new DirectoryAction(copy, directoryActionDelegate);
    }

    private void insert() {
        UserFolder insertedDirectory = directoryBuilder
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .build();
        if (insertedDirectory.getId() != null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_EXISTED);
        }

        Long id = directoryActionDelegate.createDirectory(insertedDirectory);
        directory = directoryBuilder
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(directory)
                .setChangedContent(directory.getName());
    }

    private void update() {
        UserFolder updatedDirectory = directoryBuilder
                .setUpdateTime(System.currentTimeMillis())
                .build();
        directoryActionDelegate.updateDirectory(updatedDirectory);
        directory = updatedDirectory;
        OperationContextHolder.getContext()
                .addSystemResource(updatedDirectory);
    }
}
