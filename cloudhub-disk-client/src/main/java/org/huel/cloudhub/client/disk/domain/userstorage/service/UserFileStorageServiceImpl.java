package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.userstorage.FileStreamInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.OwnerType;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorageService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.event.OnFileCreateEvent;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserDirectoryRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserFileStorageServiceImpl implements UserFileStorageService, UserStorageSearchService {
    private final StorageService storageService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserDirectoryRepository userDirectoryRepository;
    private final UserFileStorageRepository userFileStorageRepository;


    public UserFileStorageServiceImpl(StorageService storageService,
                                      ApplicationEventPublisher eventPublisher,
                                      UserDirectoryRepository userDirectoryRepository,
                                      UserFileStorageRepository userFileStorageRepository) {
        this.storageService = storageService;
        this.eventPublisher = eventPublisher;
        this.userDirectoryRepository = userDirectoryRepository;
        this.userFileStorageRepository = userFileStorageRepository;
    }

    @Override
    public Storage createDirectory(String directoryName, long parentId,
                                long owner, OwnerType ownerType) {
        UserDirectory exist = userDirectoryRepository.getByName(
                directoryName, parentId, owner, ownerType);
        if (exist != null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_EXISTED);
        }
        long time = System.currentTimeMillis();
        UserDirectory userDirectory = UserDirectory.builder()
                .setName(directoryName)
                .setOwner(owner)
                .setOwnerType(ownerType)
                .setParentId(parentId)
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
        userDirectoryRepository.insert(userDirectory);
        return userDirectory;
    }

    @Override
    public Storage uploadFile(FileStorageInfo fileStorageInfo,
                              FileStreamInfo fileStreamInfo) throws IOException {
        String id = storageService.saveFile(fileStreamInfo.inputStream());

        checkDirectoryState(fileStorageInfo.directoryId(), fileStorageInfo.storageOwner());

        UserFileStorage existUserFileStorage = userFileStorageRepository.getById(
                fileStorageInfo.storageOwner().getOwnerId(),
                fileStorageInfo.storageOwner().getOwnerType(),
                fileStorageInfo.directoryId(),
                fileStorageInfo.fileName()
        );

        long time = System.currentTimeMillis();
        if (existUserFileStorage == null) {
            UserFileStorage userFileStorage = UserFileStorage.builder()
                    .setFileId(id)
                    .setFileCategory(fileStreamInfo.fileType())
                    .setMimeType(fileStreamInfo.mimeType())
                    .setName(fileStorageInfo.fileName())
                    .setOwner(fileStorageInfo.storageOwner().getOwnerId())
                    .setOwnerType(fileStorageInfo.storageOwner().getOwnerType())
                    .setDirectoryId(fileStorageInfo.directoryId())
                    .setDeleted(false)
                    .setCreateTime(time)
                    .setUpdateTime(time)
                    .build();
            userFileStorageRepository.insert(userFileStorage);
            OnFileCreateEvent onFileCreateEvent = new OnFileCreateEvent(
                    userFileStorage);
            eventPublisher.publishEvent(onFileCreateEvent);
            return userFileStorage;
        }

        if (existUserFileStorage.getFileId().equals(id)) {
            return existUserFileStorage;
        }

        UserFileStorage updatedStorage = existUserFileStorage.toBuilder().setUpdateTime(time)
                .setDeleted(false)
                .setFileId(id)
                .setFileCategory(fileStreamInfo.fileType())
                .setMimeType(fileStreamInfo.mimeType())
                .setName(fileStorageInfo.fileName())
                .build();
        userFileStorageRepository.update(updatedStorage);
        OnFileCreateEvent onFileCreateEvent = new OnFileCreateEvent(
                updatedStorage);
        eventPublisher.publishEvent(onFileCreateEvent);

        return updatedStorage;
    }

    private void checkDirectoryState(long directoryId, StorageOwner storageOwner) {
        if (!isDirectoryExist(directoryId)) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (directoryId == UserDirectory.ROOT) {
            return;
        }

        UserDirectory userDirectory = userDirectoryRepository.getById(
                directoryId);
        if (userDirectory.getOwner() != storageOwner.getOwnerId()
                || userDirectory.getOwnerType() != storageOwner.getOwnerType()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
    }

    private boolean isDirectoryExist(long directoryId) {
        if (directoryId == UserDirectory.ROOT) {
            return true;
        }
        UserDirectory userDirectory = userDirectoryRepository.getById(
                directoryId);
        return userDirectory != null;
    }

    @Override
    public void downloadFile(long fileId, long owner,
                             OwnerType ownerType,
                             OutputStream outputStream) throws IOException {
        UserFileStorage userFileStorage = findFile(fileId);
        if (userFileStorage.getOwner() != owner
                || userFileStorage.getOwnerType() != ownerType) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }


        storageService.getFile(userFileStorage.getFileId(), outputStream);
    }

    @Override
    public void downloadFile(FileStorageInfo fileStorageInfo,
                             OutputStream outputStream) throws IOException {

    }

    @Override
    public void deleteFile(long fileId, long owner,
                           OwnerType ownerType) {

    }

    @Override
    public void deleteFile(FileStorageInfo fileStorageInfo) {

    }

    @Override
    public UserDirectory findDirectory(long directoryId) throws StorageException {
        if (directoryId == UserDirectory.ROOT) {
            return UserDirectory.ROOT_DIRECTORY;
        }
        UserDirectory userDirectory = userDirectoryRepository.getById(directoryId);
        if (userDirectory == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userDirectory;
    }

    @Override
    public UserDirectory findDirectory(FileStorageInfo fileStorageInfo) throws StorageException {
        UserDirectory userDirectory = userDirectoryRepository.getByName(
                fileStorageInfo.fileName(),
                fileStorageInfo.directoryId(),
                fileStorageInfo.storageOwner().getOwnerId(),
                fileStorageInfo.storageOwner().getOwnerType());
        if (userDirectory == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userDirectory;
    }

    @Override
    public List<UserDirectory> listDirectories(long directoryId, StorageOwner storageOwner) {
        return userDirectoryRepository.getByParentId(
                directoryId, storageOwner.getOwnerId(), storageOwner.getOwnerType());
    }

    @Override
    public UserFileStorage findFile(long fileId) throws StorageException {
        UserFileStorage userFileStorage = userFileStorageRepository.getById(fileId);
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return userFileStorage;
    }

    @Override
    public UserFileStorage findFile(FileStorageInfo fileStorageInfo) throws StorageException {
        UserFileStorage userFileStorage = userFileStorageRepository.getById(
                fileStorageInfo.storageOwner().getOwnerId(),
                fileStorageInfo.storageOwner().getOwnerType(),
                fileStorageInfo.directoryId(),
                fileStorageInfo.fileName()
        );
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return userFileStorage;
    }
}
