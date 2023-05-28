package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFolderRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class UserStorageActionServiceImpl implements StorageActionService,
        FolderActionDelegate, FileActionDelegate {
    private final UserFileStorageRepository userFileStorageRepository;
    private final UserFolderRepository userFolderRepository;

    public UserStorageActionServiceImpl(UserFileStorageRepository userFileStorageRepository,
                                        UserFolderRepository userFolderRepository) {
        this.userFileStorageRepository = userFileStorageRepository;
        this.userFolderRepository = userFolderRepository;
    }

    @Override
    public StorageAction openStorageAction(StorageIdentity storage) {
        return createStorageAction(storage);
    }

    @Override
    public StorageAction openStorageAction(StorageIdentity storageIdentity, StorageOwner storageOwner) throws StorageException {
        AttributedStorage storage = switch (storageIdentity.getStorageType()) {
            case FOLDER -> findDirectory(storageIdentity.getStorageId());
            case FILE -> findFile(storageIdentity.getStorageId());
            case LINK -> null;
        };
        if (storage == null || !checkStorageOwner(storage,
                storageOwner.getOwnerId(), storageOwner.getOwnerType())) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return createStorageAction(storage);
    }

    private boolean checkStorageOwner(Storage storage, long ownerId,
                                      LegalUserType userType) {
        return storage.getOwnerId() == ownerId &&
                storage.getOwnerType() == userType;
    }

    private StorageAction createStorageAction(StorageIdentity storage) {
        return switch (storage.getStorageType()) {
            case FOLDER -> createDirectoryAction(storage);
            case FILE -> createFileAction(storage);
            default -> null;
        };
    }

    private StorageAction createDirectoryAction(StorageIdentity storage) {
        if (storage.getStorageType() != StorageType.FOLDER) {
            throw new IllegalArgumentException("storage type must be folder");
        }
        if (!(storage instanceof UserFolder userFolder)) {
            return createDirectoryAction(
                    findDirectory(storage.getStorageId())
            );
        }

        return new FolderAction(userFolder, this);
    }

    private StorageAction createFileAction(StorageIdentity storage) {
        if (storage.getStorageType() != StorageType.FILE) {
            throw new IllegalArgumentException("storage type must be file");
        }
        if (!(storage instanceof UserFileStorage userFileStorage)) {
            return createFileAction(
                    findFile(storage.getStorageId())
            );
        }

        return new FileAction(userFileStorage, this);
    }

    private UserFileStorage findFile(long fileId) throws StorageException {
        UserFileStorage userFileStorage = userFileStorageRepository.getById(fileId);
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        if (userFileStorage.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return userFileStorage;
    }

    private UserFolder findDirectory(long directoryId) throws StorageException {
        UserFolder userFolder = userFolderRepository.getById(directoryId);
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (userFolder.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userFolder;
    }

    @Override
    public Long createDirectory(UserFolder userFolder) {
        return userFolderRepository.insert(userFolder);
    }

    @Override
    public void updateDirectory(UserFolder userFolder) {
        userFolderRepository.update(userFolder);
    }

    @Override
    public void checkExistsFolder(String name, long parentId) {
        UserFolder userFolder =
                userFolderRepository.getByName(name, parentId);
        if (userFolder != null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_EXISTED);
        }
    }

    @Override
    public Long createFile(UserFileStorage userFileStorage) {
        return userFileStorageRepository.insert(userFileStorage);
    }

    @Override
    public void updateFile(UserFileStorage userFileStorage) {
        userFileStorageRepository.update(userFileStorage);
    }

    @Override
    public void checkExistsFile(String name, long parentId) {
        UserFileStorage userFileStorage =
                userFileStorageRepository.getByName(name, parentId);
        if (userFileStorage != null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_EXISTED);
        }
    }

    @Override
    public AttributedStorage checkParentExists(long parentId) {
        if (parentId < 0) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (parentId == 0) {
            return UserFolder.ROOT_FOLDER;
        }
        UserFolder userFolder = userFolderRepository.getById(parentId);
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (userFolder.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userFolder;
    }
}
