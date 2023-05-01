package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileStreamInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageProcessor;
import org.huel.cloudhub.client.disk.domain.userstorage.UserDirectory;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorageService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserDirectoryRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserFileStorageServiceImpl implements UserFileStorageService, UserStorageSearchService {
    private static final Logger logger = LoggerFactory.getLogger(UserFileStorageServiceImpl.class);

    private final StorageService storageService;
    private final ApplicationEventPublisher eventPublisher;
    private final List<StorageProcessor> storageProcessors;
    private final UserDirectoryRepository userDirectoryRepository;
    private final UserFileStorageRepository userFileStorageRepository;

    public UserFileStorageServiceImpl(StorageService storageService,
                                      ApplicationEventPublisher eventPublisher,
                                      List<StorageProcessor> storageProcessors,
                                      UserDirectoryRepository userDirectoryRepository,
                                      UserFileStorageRepository userFileStorageRepository) {
        this.storageService = storageService;
        this.eventPublisher = eventPublisher;
        this.storageProcessors = storageProcessors;
        this.userDirectoryRepository = userDirectoryRepository;
        this.userFileStorageRepository = userFileStorageRepository;
    }

    @Override
    public AttributedStorage createDirectory(String directoryName, long parentId,
                                             long owner, LegalUserType legalUserType) throws StorageException {
        UserDirectory exist = userDirectoryRepository.getByName(
                directoryName, parentId, owner, legalUserType);
        if (exist != null && !exist.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_EXISTED);
        }
        long time = System.currentTimeMillis();
        if (exist != null && exist.isDeleted()) {
            UserDirectory updated = exist
                    .toBuilder()
                    .setUpdateTime(time)
                    .setDeleted(false)
                    .build();
            userDirectoryRepository.update(updated);
            OperationContextHolder.getContext()
                    .addSystemResource(updated)
                    .setChangedContent(updated.getName());
            return updated;
        }

        UserDirectory userDirectory = UserDirectory.builder()
                .setName(directoryName)
                .setOwner(owner)
                .setOwnerType(legalUserType)
                .setParentId(parentId)
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
        long id = userDirectoryRepository.insert(userDirectory);
        UserDirectory inserted = userDirectory.toBuilder()
                .setId(id)
                .build();
        logger.debug("create directory: {}", inserted);
        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .setChangedContent(inserted.getName());

        return inserted;
    }

    @Override
    public void deleteDirectory(long directoryId, long owner,
                                LegalUserType legalUserType) {

    }

    @Override
    public void renameDirectory(long directoryId, String newName,
                                long owner, LegalUserType legalUserType) {

    }

    @Override
    public void moveDirectory(long directoryId, long newParentId,
                              long owner, LegalUserType legalUserType) {

    }

    @Override
    public AttributedStorage uploadFile(FileStorageInfo fileStorageInfo,
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
            long storageId = userFileStorageRepository.insert(userFileStorage);
            UserFileStorage updatedStorage = userFileStorage.toBuilder()
                    .setId(storageId)
                    .build();
            OperationContextHolder.getContext()
                    .addSystemResource(updatedStorage)
                    .setChangedContent(updatedStorage.getName());

            StorageAttr storageAttr = new StorageAttr(
                    updatedStorage.getName(),
                    null,
                    null,
                    fileStreamInfo.fileType()
            );
            dispatchFileOnCreate(updatedStorage, storageAttr);

            return updatedStorage;
        }

        if (existUserFileStorage.getFileId().equals(id)) {
            return existUserFileStorage;
        }

        UserFileStorage updatedStorage = existUserFileStorage.toBuilder()
                .setUpdateTime(time)
                .setDeleted(false)
                .setFileId(id)
                .setFileCategory(fileStreamInfo.fileType())
                .setMimeType(fileStreamInfo.mimeType())
                .setName(fileStorageInfo.fileName())
                .build();
        userFileStorageRepository.update(updatedStorage);
        OperationContextHolder.getContext()
                .addSystemResource(updatedStorage)
                .setChangedContent(updatedStorage.getName());

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
        if (userDirectory.isDeleted()) {
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
                             LegalUserType legalUserType,
                             OutputStream outputStream) throws IOException {
        UserFileStorage userFileStorage = findFile(fileId);
        if (userFileStorage.getOwner() != owner
                || userFileStorage.getOwnerType() != legalUserType) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }


        storageService.getFile(userFileStorage.getFileId(), outputStream);
    }

    @Override
    public void downloadFile(FileStorageInfo fileStorageInfo,
                             OutputStream outputStream) throws IOException {
        UserFileStorage userFileStorage = findFile(fileStorageInfo);

        if (userFileStorage.getOwner() != fileStorageInfo.storageOwner().getOwnerId()
                || userFileStorage.getOwnerType() != fileStorageInfo.storageOwner().getOwnerType()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }

        storageService.getFile(userFileStorage.getFileId(), outputStream);
    }

    @Override
    public void deleteFile(long fileId, long owner,
                           LegalUserType legalUserType) {
        UserFileStorage userFileStorage = findFile(fileId);

        if (userFileStorage.getOwner() != owner
                || userFileStorage.getOwnerType() != legalUserType) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }


        long time = System.currentTimeMillis();
        UserFileStorage updatedStorage = userFileStorage.toBuilder()
                .setDeleted(true)
                .setUpdateTime(time)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(updatedStorage)
                .setChangedContent(updatedStorage.getName());

        userFileStorageRepository.update(updatedStorage);
    }

    @Override
    public void deleteFile(FileStorageInfo fileStorageInfo) {
        UserFileStorage userFileStorage = findFile(fileStorageInfo);
        long time = System.currentTimeMillis();
        UserFileStorage updatedStorage = userFileStorage.toBuilder()
                .setDeleted(true)
                .setUpdateTime(time)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(updatedStorage)
                .setChangedContent(updatedStorage.getName());

        userFileStorageRepository.update(updatedStorage);
    }

    @NonNull
    @Override
    public UserDirectory findDirectory(long directoryId) throws StorageException {
        if (directoryId == UserDirectory.ROOT) {
            return UserDirectory.ROOT_DIRECTORY;
        }
        UserDirectory userDirectory = userDirectoryRepository.getById(directoryId);
        if (userDirectory == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (userDirectory.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userDirectory;
    }

    @NonNull
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
        if (userDirectory.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return userDirectory;
    }

    @NonNull
    private List<UserDirectory> listDirectories(long directoryId, StorageOwner storageOwner) {
        return userDirectoryRepository.getByParentId(
                directoryId,
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType());
    }

    private List<UserFileStorage> listOnlyFiles(long directoryId, StorageOwner storageOwner) {
        return userFileStorageRepository.getByDirectoryId(
                directoryId,
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType()
        );
    }

    @NonNull
    @Override
    public UserFileStorage findFile(long fileId) throws StorageException {
        UserFileStorage userFileStorage = userFileStorageRepository.getById(fileId);
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        if (userFileStorage.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return userFileStorage;
    }

    @NonNull
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
        if (userFileStorage.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return userFileStorage;
    }

    @Override
    public List<AttributedStorage> listFiles(long directoryId,
                                             StorageOwner storageOwner) {
        List<UserFileStorage> userFileStorages = listOnlyFiles(directoryId, storageOwner);
        List<UserDirectory> userDirectories = listDirectories(directoryId, storageOwner);

        List<AttributedStorage> attributedStorages = new ArrayList<>(userDirectories);
        attributedStorages.addAll(userFileStorages);

        return attributedStorages;
    }

    private void dispatchFileOnCreate(Storage storage, StorageAttr storageAttr) {
        storageProcessors.forEach(storageProcessor ->
                storageProcessor.onStorageCreated(storage, storageAttr));
    }
}
