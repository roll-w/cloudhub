package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserDirectoryRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class UserStorageActionServiceImpl implements StorageActionService,
        DirectoryActionDelegate, FileActionDelegate {
    private final UserFileStorageRepository userFileStorageRepository;
    private final UserDirectoryRepository userDirectoryRepository;

    public UserStorageActionServiceImpl(UserFileStorageRepository userFileStorageRepository,
                                        UserDirectoryRepository userDirectoryRepository) {
        this.userFileStorageRepository = userFileStorageRepository;
        this.userDirectoryRepository = userDirectoryRepository;
    }

    @Override
    public StorageAction openStorageAction(Storage storage) {
        return createStorageAction(storage);
    }

    @Override
    public StorageAction openStorageAction(long storageId,
                                           StorageType storageType)
            throws StorageException {
        AttributedStorage storage = switch (storageType) {
            case FOLDER -> findDirectory(storageId);
            case FILE -> findFile(storageId);
            case LINK -> null;
        };
        if (storage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return createStorageAction(storage);
    }

    @Override
    public StorageAction openStorageAction(long storageId, StorageType storageType,
                                           long ownerId, LegalUserType userType)
            throws StorageException {
        AttributedStorage storage = switch (storageType) {
            case FOLDER -> findDirectory(storageId);
            case FILE -> findFile(storageId);
            case LINK -> null;
        };
        if (storage == null || !checkStorageOwner(storage, ownerId, userType)) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return createStorageAction(storage);
    }

    private boolean checkStorageOwner(Storage storage, long ownerId,
                                      LegalUserType userType) {
        return storage.getOwnerId() == ownerId &&
                storage.getOwnerType() == userType;
    }

    private StorageAction createStorageAction(Storage storage) {
        return switch (storage.getStorageType()) {
            case FOLDER -> createDirectoryAction(storage);
            case FILE -> createFileAction(storage);
            default -> null;
        };
    }

    private StorageAction createDirectoryAction(Storage storage) {
        if (storage.getStorageType() != StorageType.FOLDER) {
            throw new IllegalArgumentException("storage type must be folder");
        }
        if (!(storage instanceof UserDirectory userDirectory)) {
            return createDirectoryAction(
                    findDirectory(storage.getStorageId())
            );
        }

        return new DirectoryAction(userDirectory, this);
    }

    private StorageAction createFileAction(Storage storage) {
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

    private UserDirectory findDirectory(long directoryId) throws StorageException {
        UserDirectory userDirectory = userDirectoryRepository.getById(directoryId);
        if (userDirectory == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (userDirectory.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userDirectory;
    }

    @Override
    public Long createDirectory(UserDirectory userDirectory) {
        return userDirectoryRepository.insert(userDirectory);
    }

    @Override
    public void updateDirectory(UserDirectory userDirectory) {
        userDirectoryRepository.update(userDirectory);
    }

    @Override
    public Long createFile(UserFileStorage userFileStorage) {
        return userFileStorageRepository.insert(userFileStorage);
    }

    @Override
    public void updateFile(UserFileStorage userFileStorage) {
        userFileStorageRepository.update(userFileStorage);
    }
}
