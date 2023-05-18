package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.storage.StorageService;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileStreamInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageProcessor;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorageService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageErrorCode;
import org.huel.cloudhub.client.disk.domain.userstorage.common.StorageException;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FolderStructureInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFolderRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.util.StorageNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserFileStorageServiceImpl implements
        UserFileStorageService, UserStorageSearchService {
    private static final Logger logger = LoggerFactory.getLogger(UserFileStorageServiceImpl.class);

    private final StorageService storageService;
    private final List<StorageProcessor> storageProcessors;
    private final UserFolderRepository userFolderRepository;
    private final UserFileStorageRepository userFileStorageRepository;

    public UserFileStorageServiceImpl(StorageService storageService,
                                      List<StorageProcessor> storageProcessors,
                                      UserFolderRepository userFolderRepository,
                                      UserFileStorageRepository userFileStorageRepository) {
        this.storageService = storageService;
        this.storageProcessors = storageProcessors;
        this.userFolderRepository = userFolderRepository;
        this.userFileStorageRepository = userFileStorageRepository;
    }

    @Override
    public AttributedStorage createFolder(String folderNameParam, long parentId,
                                          StorageOwner storageOwner) throws StorageException {
        String folderName = StorageNameValidator.validate(folderNameParam);

        UserFolder exist = userFolderRepository.getByName(
                folderName, parentId,
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType());
        if (exist != null && !exist.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_EXISTED);
        }
        checkParent(parentId);

        long time = System.currentTimeMillis();
        if (exist != null && exist.isDeleted()) {
            UserFolder updated = exist
                    .toBuilder()
                    .setUpdateTime(time)
                    .setDeleted(false)
                    .build();
            userFolderRepository.update(updated);
            OperationContextHolder.getContext()
                    .addSystemResource(updated)
                    .setChangedContent(updated.getName());
            return updated;
        }

        UserFolder userFolder = UserFolder.builder()
                .setName(folderName)
                .setOwner(storageOwner.getOwnerId())
                .setOwnerType(storageOwner.getOwnerType())
                .setParentId(parentId)
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
        long id = userFolderRepository.insert(userFolder);
        UserFolder inserted = userFolder.toBuilder()
                .setId(id)
                .build();
        logger.debug("create directory: {}", inserted);
        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .setChangedContent(inserted.getName());

        return inserted;
    }

    private void checkParent(long parentId) {
        if (parentId == UserFolder.ROOT) {
            return;
        }

        UserFolder userFolder =
                userFolderRepository.getById(parentId);
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST,
                    "Parent folder not exist");
        }
    }

    @Override
    public AttributedStorage uploadFile(FileStorageInfo fileStorageInfo,
                                        FileStreamInfo fileStreamInfo) throws IOException {
        String cfsFileId = storageService.saveFile(fileStreamInfo.inputStream());
        String fileName = StorageNameValidator.validate(fileStorageInfo.fileName());

        checkDirectoryState(fileStorageInfo.folderId(), fileStorageInfo.storageOwner());

        UserFileStorage existUserFileStorage = userFileStorageRepository.getById(
                fileStorageInfo.storageOwner().getOwnerId(),
                fileStorageInfo.storageOwner().getOwnerType(),
                fileStorageInfo.folderId(),
                fileName
        );

        long time = System.currentTimeMillis();
        if (existUserFileStorage == null) {
            UserFileStorage userFileStorage = UserFileStorage.builder()
                    .setFileId(cfsFileId)
                    .setFileCategory(fileStreamInfo.fileType())
                    .setMimeType(fileStreamInfo.mimeType())
                    .setName(fileName)
                    .setOwner(fileStorageInfo.storageOwner().getOwnerId())
                    .setOwnerType(fileStorageInfo.storageOwner().getOwnerType())
                    .setFolderId(fileStorageInfo.folderId())
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

            dispatchFileOnCreate(updatedStorage, fileStreamInfo,
                    OperationContextHolder.getContext().getOperator());
            return updatedStorage;
        }

        if (existUserFileStorage.getFileId().equals(cfsFileId) && !existUserFileStorage.isDeleted()) {
            return existUserFileStorage;
        }

        UserFileStorage updatedStorage = existUserFileStorage.toBuilder()
                .setUpdateTime(time)
                .setDeleted(false)
                .setFileId(cfsFileId)
                .setFileCategory(fileStreamInfo.fileType())
                .setMimeType(fileStreamInfo.mimeType())
                .setName(fileName)
                .build();
        userFileStorageRepository.update(updatedStorage);
        OperationContextHolder.getContext()
                .addSystemResource(updatedStorage)
                .setChangedContent(updatedStorage.getName());

        dispatchFileOnCreate(updatedStorage, fileStreamInfo,
                OperationContextHolder.getContext().getOperator());

        return updatedStorage;
    }

    private void dispatchFileOnCreate(UserFileStorage userFileStorage,
                                      FileStreamInfo fileStreamInfo,
                                      Operator operator) {
        StorageAttr storageAttr = new StorageAttr(
                userFileStorage.getName(),
                null,
                null,
                fileStreamInfo.fileType(),
                userFileStorage.getFileId(),
                operator
        );
        dispatchFileOnCreate(userFileStorage, storageAttr);
    }

    private void checkDirectoryState(long directoryId, StorageOwner storageOwner) {
        if (!isDirectoryExist(directoryId)) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (directoryId == UserFolder.ROOT) {
            return;
        }

        UserFolder userFolder = userFolderRepository.getById(
                directoryId);
        if (userFolder.getOwner() != storageOwner.getOwnerId()
                || userFolder.getOwnerType() != storageOwner.getOwnerType()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        if (userFolder.isDeleted()) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
    }

    private boolean isDirectoryExist(long directoryId) {
        if (directoryId == UserFolder.ROOT) {
            return true;
        }
        UserFolder userFolder = userFolderRepository.getById(
                directoryId);
        return userFolder != null;
    }

    @Override
    public void downloadFile(long fileId,
                             StorageOwner storageOwner,
                             OutputStream outputStream) throws IOException {
        FileInfo userFileStorage = findFile(fileId);
        if (userFileStorage.getOwnerId() != storageOwner.getOwnerId()
                || userFileStorage.getOwnerType() != storageOwner.getOwnerType()) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }


        storageService.getFile(userFileStorage.getFileId(), outputStream);
    }

    @Override
    public AttributedStorage findStorage(StorageIdentity storageIdentity) throws StorageException {
        return switch (storageIdentity.getStorageType()) {
            case FILE -> findFile(storageIdentity.getStorageId());
            case FOLDER -> findFolder(storageIdentity.getStorageId());
            default -> throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        };
    }

    @Override
    public AttributedStorage findStorage(StorageIdentity storageIdentity,
                                         StorageOwner storageOwner) throws StorageException {
        return switch (storageIdentity.getStorageType()) {
            case FILE -> findFile(storageIdentity.getStorageId(), storageOwner);
            case FOLDER -> findFolder(storageIdentity.getStorageId(), storageOwner);
            default -> throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        };
    }

    @NonNull
    @Override
    public FolderStructureInfo findFolder(long folderId) throws StorageException {
        if (folderId == UserFolder.ROOT) {
            return FolderStructureInfo.ROOT_FOLDER;
        }
        UserFolder userFolder = userFolderRepository.getById(folderId);
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        List<UserFolder> userFolders =
                userFolderRepository.getParents(userFolder.getId());
        List<FolderInfo> folderInfos = userFolders.stream()
                .map(FolderInfo::of)
                .toList();
        return FolderStructureInfo.of(userFolder, folderInfos);
    }

    @Override
    public FolderStructureInfo findFolder(long folderId,
                                          StorageOwner storageOwner) throws StorageException {
        if (folderId == UserFolder.ROOT) {
            return FolderStructureInfo.ROOT_FOLDER;
        }
        UserFolder userFolder = userFolderRepository.getById(
                folderId,
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType());
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        List<UserFolder> userFolders =
                userFolderRepository.getParents(userFolder.getId());
        List<FolderInfo> folderInfos = userFolders.stream()
                .map(FolderInfo::of)
                .toList();
        return FolderStructureInfo.of(userFolder, folderInfos);
    }

    @NonNull
    @Override
    public FolderStructureInfo findFolder(FileStorageInfo fileStorageInfo) throws StorageException {
        String folderName = StorageNameValidator.validate(fileStorageInfo.fileName());

        UserFolder userFolder = userFolderRepository.getByName(
                folderName,
                fileStorageInfo.folderId(),
                fileStorageInfo.storageOwner().getOwnerId(),
                fileStorageInfo.storageOwner().getOwnerType()
        );
        if (userFolder == null) {
            throw new StorageException(StorageErrorCode.ERROR_DIRECTORY_NOT_EXIST);
        }
        return FolderStructureInfo.of(userFolder, List.of());
    }

    @NonNull
    private List<UserFolder> listDirectories(long directoryId, StorageOwner storageOwner) {
        return userFolderRepository.getByParentId(
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
    public FileInfo findFile(long fileId) throws StorageException {
        UserFileStorage userFileStorage = userFileStorageRepository.getById(fileId);
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return FileInfo.from(userFileStorage);
    }

    @Override
    public FileInfo findFile(long fileId, StorageOwner storageOwner) throws StorageException {
        UserFileStorage userFileStorage = userFileStorageRepository.getById(
                fileId,
                storageOwner.getOwnerId(),
                storageOwner.getOwnerType()
        );
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return FileInfo.from(userFileStorage);
    }

    @NonNull
    @Override
    public FileInfo findFile(FileStorageInfo fileStorageInfo) throws StorageException {
        String fileName = StorageNameValidator.validate(fileStorageInfo.fileName());

        UserFileStorage userFileStorage = userFileStorageRepository.getById(
                fileStorageInfo.storageOwner().getOwnerId(),
                fileStorageInfo.storageOwner().getOwnerType(),
                fileStorageInfo.folderId(),
                fileName
        );
        if (userFileStorage == null) {
            throw new StorageException(StorageErrorCode.ERROR_FILE_NOT_EXIST);
        }
        return FileInfo.from(userFileStorage);
    }

    @Override
    public List<AttributedStorage> listFiles(long folderId,
                                             StorageOwner storageOwner) {
        List<UserFileStorage> userFileStorages = listOnlyFiles(folderId, storageOwner);
        List<UserFolder> userDirectories = listDirectories(folderId, storageOwner);

        List<AttributedStorage> attributedStorages = new ArrayList<>(userDirectories);
        attributedStorages.addAll(userFileStorages);

        return attributedStorages;
    }

    @Override
    public List<AttributedStorage> listFiles(long folderId) {
        List<UserFileStorage> userFileStorages =
                userFileStorageRepository.getByDirectoryId(folderId);
        List<UserFolder> userDirectories =
                userFolderRepository.getByParentId(folderId);
        List<AttributedStorage> attributedStorages = new ArrayList<>(userDirectories);
        attributedStorages.addAll(userFileStorages);

        return attributedStorages;
    }

    private void dispatchFileOnCreate(Storage storage, StorageAttr storageAttr) {
        storageProcessors.forEach(storageProcessor ->
                storageProcessor.onStorageCreated(storage, storageAttr));
    }
}
