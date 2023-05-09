package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAttributesService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageTagValue;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.StorageMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class StorageAttrsService implements StorageAttributesService {
    private final UserStorageSearchService userStorageSearchService;
    private final StorageMetadataRepository storageMetadataRepository;

    public static final String FILE_TYPE = "fileType";

    public StorageAttrsService(UserStorageSearchService userStorageSearchService,
                               StorageMetadataRepository storageMetadataRepository) {
        this.userStorageSearchService = userStorageSearchService;
        this.storageMetadataRepository = storageMetadataRepository;
    }

    @Override
    public List<StorageTagValue> getStorageTags(StorageIdentity storageIdentity,
                                                StorageOwner storageOwner) {
        AttributedStorage storage =
                userStorageSearchService.findStorage(storageIdentity, storageOwner);
        if (!storage.getStorageType().isFile()) {
            return List.of(getStorageTagValue(storage));
        }
        StorageTagValue fileTypeTagValue = getStorageTagValue(storage);
        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByStorageId(storageIdentity.getStorageId());
        if (storageMetadata.isEmpty()) {
            return List.of(fileTypeTagValue);
        }
        List<StorageTagValue> tagValues = new ArrayList<>();
        tagValues.add(fileTypeTagValue);
        tagValues.addAll(
                storageMetadata.stream()
                        .map(StorageTagValue::of)
                        .toList()
        );
        return tagValues;
    }

    private StorageTagValue getStorageTagValue(AttributedStorage storage) {
        if (!storage.getStorageType().isFile()) {
            return StorageTagValue.of(
                    FILE_TYPE,
                    storage.getStorageType().name()
            );
        }
        return StorageTagValue.of(
                FILE_TYPE,
                storage.getFileType().name()
        );
    }
}
