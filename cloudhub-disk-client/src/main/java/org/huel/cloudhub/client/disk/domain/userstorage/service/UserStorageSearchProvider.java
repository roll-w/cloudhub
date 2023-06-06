package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.storagesearch.SearchCondition;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchConditionGroup;
import org.huel.cloudhub.client.disk.domain.storagesearch.StorageSearchConditionProvider;
import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagValue;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.StorageMetadataRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserStorageSearchCondition;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserStorageSearchRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class UserStorageSearchProvider implements StorageCategoryService,
        StorageSearchConditionProvider {
    public static final String NAME = "name";
    public static final String TIME = "time";
    public static final String SIZE = "size";
    public static final String TYPE = "type";

    private static final String[] SUPPORTED_CONDITIONS = {
            NAME, TIME, SIZE, TYPE
    };

    private final StorageMetadataRepository storageMetadataRepository;
    private final UserFileStorageRepository userFileStorageRepository;
    private final UserStorageSearchRepository userStorageSearchRepository;

    public UserStorageSearchProvider(StorageMetadataRepository storageMetadataRepository,
                                     UserFileStorageRepository userFileStorageRepository,
                                     UserStorageSearchRepository userStorageSearchRepository) {
        this.storageMetadataRepository = storageMetadataRepository;
        this.userFileStorageRepository = userFileStorageRepository;
        this.userStorageSearchRepository = userStorageSearchRepository;
    }

    @Override
    public List<? extends AttributedStorage> getStorages(SearchConditionGroup conditionGroup,
                                                         StorageOwner storageOwner)
            throws SearchConditionException {
        SearchCondition nameCondition = conditionGroup.getCondition(NAME);
        SearchCondition timeCondition = conditionGroup.getCondition(TIME);
        SearchCondition typeCondition = conditionGroup.getCondition(TYPE);

        StorageType storageType = tryParseStorageType(typeCondition);
        FileType fileType = tryParseFileType(typeCondition);

        UserStorageSearchCondition userStorageSearchCondition = new UserStorageSearchCondition(
                storageType,
                storageOwner,
                nameCondition == null ? null : nameCondition.keyword(),
                fileType,
                null, null, null
        );

        return userStorageSearchRepository.findStoragesBy(userStorageSearchCondition);
    }

    private StorageType tryParseStorageType(SearchCondition condition) {
        if (condition == null) {
            return null;
        }
        return StorageType.from(condition.keyword());
    }

    private FileType tryParseFileType(SearchCondition condition) {
        if (condition == null) {
            return null;
        }
        return FileType.from(condition.keyword());
    }

    private record TimePair(
            Long before,
            Long after
    ) {
    }


    @Override
    public boolean supportsCondition(String name) {
        for (String supportedCondition : SUPPORTED_CONDITIONS) {
            if (supportedCondition.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AttributedStorage> getByType(StorageOwner storageOwner,
                                             FileType fileType) {
        return Collections.unmodifiableList(
                userFileStorageRepository.getByType(
                        storageOwner.getOwnerId(),
                        storageOwner.getOwnerType(),
                        fileType
                )
        );
    }

    @Override
    public List<AttributedStorage> getByTags(StorageOwner storageOwner, List<TagValue> tagValues) {
        if (checkDuplicateTag(tagValues)) {
            return List.of();
        }
        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByTagValues(tagValues);
        List<Long> storageIds = storageMetadata.stream()
                .map(StorageMetadata::getStorageId)
                .distinct()
                .toList();

        return Collections.unmodifiableList(
                userFileStorageRepository.getByIds(storageIds, storageOwner)
        );
    }

    @Override
    public List<AttributedStorage> getByTypeAndTags(StorageOwner storageOwner,
                                                    FileType fileType,
                                                    List<TagValue> tagValues) {
        if (checkDuplicateTag(tagValues)) {
            return List.of();
        }
        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByTagValues(tagValues);
        List<Long> storageIds = storageMetadata.stream()
                .map(StorageMetadata::getStorageId)
                .distinct()
                .toList();

        List<UserFileStorage> storages =
                userFileStorageRepository.getByIdsAndType(storageIds, fileType, storageOwner);
        return Collections.unmodifiableList(storages);
    }

    private boolean checkDuplicateTag(List<TagValue> tagValues) {
        return tagValues
                .stream()
                .map(TagValue::name)
                .distinct()
                .count() == tagValues.size();
    }


}
