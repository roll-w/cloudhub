package org.huel.cloudhub.client.disk.domain.versioned.service;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageEventListener;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileService;
import org.huel.cloudhub.client.disk.domain.versioned.VersionedFileStorage;
import org.huel.cloudhub.client.disk.domain.versioned.repository.VersionedFileRepository;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class VersionedFileServiceImpl implements VersionedFileService,
        StorageEventListener {
    private final VersionedFileRepository versionedFileRepository;

    public VersionedFileServiceImpl(VersionedFileRepository versionedFileRepository) {
        this.versionedFileRepository = versionedFileRepository;
    }

    @Override
    public void onStorageCreated(@NonNull AttributedStorage storage, StorageAttr storageAttr) {
        if (storage.getStorageType() != StorageType.FILE) {
            return;
        }
        VersionedFileStorage versionedFileStorage =
                versionedFileRepository.getLatestFileVersion(storage.getStorageId());
        if (versionedFileStorage == null) {
            VersionedFileStorage newVersionedFileStorage = VersionedFileStorage.builder()
                    .setFileId(storageAttr.fileId())
                    .setVersion(1)
                    .setOperator(storageAttr.operator().getOperatorId())
                    .setStorageId(storage.getStorageId())
                    .setStorageType(storage.getStorageType())
                    .setCreateTime(System.currentTimeMillis())
                    .setDeleted(false)
                    .build();
            versionedFileRepository.insert(newVersionedFileStorage);
            return;
        }
        long version = versionedFileStorage.getVersion() + 1;
        VersionedFileStorage newVersionedFileStorage = VersionedFileStorage.builder()
                .setFileId(versionedFileStorage.getFileId())
                .setVersion(version)
                .setStorageId(storage.getStorageId())
                .setStorageType(storage.getStorageType())
                .setOperator(storageAttr.operator().getOperatorId())
                .setCreateTime(System.currentTimeMillis())
                .setDeleted(false)
                .build();
        versionedFileRepository.insert(newVersionedFileStorage);
    }

    @Override
    public VersionedFileStorage getVersionedFileStorage(
            long versionedFileStorageId) {
        VersionedFileStorage storage =
                versionedFileRepository.getById(versionedFileStorageId);
        if (storage == null || storage.isDeleted()) {
            return null;
        }
        return storage;
    }

    @Override
    public List<VersionedFileStorage> getVersionedFileStorages(
            long fileStorageId) {
        return versionedFileRepository.getFileVersions(fileStorageId);
    }

    @Override
    public void deleteVersionedFileStorage(long versionedFileStorageId) {
        VersionedFileStorage versionedFileStorage =
                versionedFileRepository.getById(versionedFileStorageId);
        if (versionedFileStorage == null || versionedFileStorage.isDeleted()) {
            return;
        }
        VersionedFileStorage updated = versionedFileStorage
                .toBuilder()
                .setDeleted(true)
                .build();
        versionedFileRepository.update(updated);
    }

    @Override
    public void deleteVersionedFileStorage(long storageId, long version) {
        VersionedFileStorage versionedFileStorage =
                versionedFileRepository.getFileVersion(storageId, version);
        if (versionedFileStorage == null || versionedFileStorage.isDeleted()) {
            return;
        }
        VersionedFileStorage updated = versionedFileStorage
                .toBuilder()
                .setDeleted(true)
                .build();
        versionedFileRepository.update(updated);
    }
}
