package org.huel.cloudhub.client.disk.domain.userstorage.service;

import com.google.common.base.Strings;
import org.cloudhub.util.TimeParser;
import org.cloudhub.util.TimeRange;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchCondition;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchConditionGroup;
import org.huel.cloudhub.client.disk.domain.storagesearch.StorageSearchConditionProvider;
import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchExpressionException;
import org.huel.cloudhub.client.disk.domain.tag.NameValue;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.StorageMetadataRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserFileStorageRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserStorageSearchCondition;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.UserStorageSearchRepository;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.Collections;
import java.util.List;

import static org.huel.cloudhub.client.disk.domain.userstorage.common.ConditionNames.*;

/**
 * @author RollW
 */
@Service
public class UserStorageSearchProvider implements StorageCategoryService,
        StorageSearchConditionProvider {

    private static final String[] SUPPORTED_CONDITIONS = {
            NAME, TIME, LAST_MODIFIED_TIME, SIZE, TYPE
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
        SearchCondition timeCondition = getTimeCondition(conditionGroup);
        SearchCondition typeCondition = conditionGroup.getCondition(TYPE);

        StorageType storageType = tryParseStorageType(typeCondition);
        FileType fileType = tryParseFileType(typeCondition);

        TimeRange timeRange = tryParseTimeRange(timeCondition);

        UserStorageSearchCondition userStorageSearchCondition = new UserStorageSearchCondition(
                storageType,
                storageOwner,
                nameCondition == null ? null : nameCondition.keyword(),
                fileType,
                null,
                timeRange.end(),
                timeRange.start()
        );

        return userStorageSearchRepository.findStoragesBy(userStorageSearchCondition);
    }

    private SearchCondition getTimeCondition(SearchConditionGroup conditionGroup) {
        SearchCondition timeCondition = conditionGroup.getCondition(TIME);
        if (timeCondition == null) {
            return conditionGroup.getCondition(LAST_MODIFIED_TIME);
        }
        return timeCondition;
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

    @NonNull
    private TimeRange tryParseTimeRange(SearchCondition condition) {
        if (condition == null) {
            return TimeRange.NULL;
        }
        if (Strings.isNullOrEmpty(condition.keyword())) {
            throw new SearchExpressionException("time condition keyword is null or empty");
        }

        return TimeParser.parseTimeRange(condition.keyword());
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
    public List<AttributedStorage> getByTags(StorageOwner storageOwner, List<NameValue> nameValues) {
        if (checkDuplicateTag(nameValues)) {
            return List.of();
        }
        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByTagValues(nameValues);
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
                                                    List<NameValue> nameValues) {
        if (checkDuplicateTag(nameValues)) {
            return List.of();
        }
        List<StorageMetadata> storageMetadata =
                storageMetadataRepository.getByTagValues(nameValues);
        List<Long> storageIds = storageMetadata.stream()
                .map(StorageMetadata::getStorageId)
                .distinct()
                .toList();

        List<UserFileStorage> storages =
                userFileStorageRepository.getByIdsAndType(storageIds, fileType, storageOwner);
        return Collections.unmodifiableList(storages);
    }

    private boolean checkDuplicateTag(List<NameValue> nameValues) {
        return nameValues
                .stream()
                .map(NameValue::name)
                .distinct()
                .count() != nameValues.size();
    }


}
