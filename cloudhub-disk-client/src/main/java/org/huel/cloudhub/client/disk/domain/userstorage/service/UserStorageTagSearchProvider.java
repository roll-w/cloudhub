package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.storagesearch.SearchCondition;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchConditionGroup;
import org.huel.cloudhub.client.disk.domain.storagesearch.StorageSearchConditionProvider;
import org.huel.cloudhub.client.disk.domain.storagesearch.common.SearchConditionException;
import org.huel.cloudhub.client.disk.domain.tag.InternalTagGroupRepository;
import org.huel.cloudhub.client.disk.domain.tag.TagEventListener;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagValue;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.common.ConditionNames;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author RollW
 */
@Service
public class UserStorageTagSearchProvider implements StorageSearchConditionProvider, TagEventListener {
    private final Set<String> supportedTagNames = new HashSet<>();
    private final InternalTagGroupRepository tagGroupRepository;
    private final StorageCategoryService storageCategoryService;

    public UserStorageTagSearchProvider(InternalTagGroupRepository tagGroupRepository,
                                        StorageCategoryService storageCategoryService) {
        this.tagGroupRepository = tagGroupRepository;
        this.storageCategoryService = storageCategoryService;
        init();
    }

    private void init() {
        tagGroupRepository.findAll().forEach(
                tagGroup -> supportedTagNames.add(tagGroup.getName())
        );
    }

    @Override
    public List<? extends AttributedStorage> getStorages(
            SearchConditionGroup conditionGroup, StorageOwner storageOwner) throws SearchConditionException {
        SearchCondition typeCondition = conditionGroup.getCondition(ConditionNames.TYPE);
        StorageType storageType = StorageType.valueOf(typeCondition.keyword());
        if (!isValidType(storageType)) {
            return List.of();
        }
        FileType fileType = FileType.from(typeCondition.keyword());
        List<TagValue> tagValues = extractTagValues(conditionGroup);
        if (fileType == null) {
            return storageCategoryService.getByTags(storageOwner, tagValues);
        }
        return storageCategoryService.getByTypeAndTags(storageOwner, fileType, tagValues);
    }

    private boolean isValidType(StorageType storageType) {
        if (storageType == null) {
            return true;
        }
        return storageType == StorageType.FILE;
    }

    private List<TagValue> extractTagValues(SearchConditionGroup conditionGroup) {
        List<TagValue> result = new ArrayList<>();
        for (String supportedTagName : supportedTagNames) {
            SearchCondition searchCondition =
                    conditionGroup.getCondition(supportedTagName);
            if (searchCondition == null) {
                continue;
            }
            result.add(new TagValue(supportedTagName, searchCondition.keyword()));
        }

        return result;
    }

    @Override
    public boolean supportsCondition(String name) {
        return supportedTagNames.contains(name);
    }

    @Override
    public void onTagGroupChanged(TagGroupDto tagGroupDto) {
        supportedTagNames.add(tagGroupDto.name());
    }

    @Override
    public void onTagGroupDelete(String tagGroupName) {
        supportedTagNames.remove(tagGroupName);
    }
}
