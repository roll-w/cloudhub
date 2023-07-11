package org.huel.cloudhub.client.disk.domain.userstorage.service;

import org.huel.cloudhub.client.disk.domain.tag.ContentTagProvider;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.TaggedValue;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageTagValue;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.StorageMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class StorageAttrsService implements StorageAttributesService {
    private final UserStorageSearchService userStorageSearchService;
    private final ContentTagProvider contentTagProvider;
    private final StorageMetadataRepository storageMetadataRepository;

    public static final String FILE_TYPE = "fileType";

    public StorageAttrsService(UserStorageSearchService userStorageSearchService,
                               ContentTagProvider contentTagProvider,
                               StorageMetadataRepository storageMetadataRepository) {
        this.userStorageSearchService = userStorageSearchService;
        this.contentTagProvider = contentTagProvider;
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

        List<TaggedValue> taggedValues = getTagValues(storageMetadata);
        tagValues.addAll(
                pairWithIdTagValues(taggedValues, storageMetadata)
        );
        return tagValues;
    }

    private List<StorageTagValue> pairWithIdTagValues(List<TaggedValue> taggedValues,
                                                      List<StorageMetadata> metadatas) {
        List<StorageTagValue> tagValues = new ArrayList<>();
        for (StorageMetadata metadata : metadatas) {
            TaggedValue taggedValue = findInIdTagValues(taggedValues, metadata);
            tagValues.add(StorageTagValue.of(
                    metadata,
                    taggedValue.name(),
                    taggedValue.value()
            ));
        }
        return tagValues;
    }

    private TaggedValue findInIdTagValues(List<TaggedValue> taggedValues, StorageMetadata metadata) {
        return taggedValues.stream()
                .filter(taggedValue -> taggedValue.groupId() == metadata.getTagGroupId())
                .findFirst()
                .orElse(null);
    }

    private List<TaggedValue> getTagValues(List<StorageMetadata> storageMetadata) {
        List<Long> tagGroupIds = storageMetadata.stream()
                .map(StorageMetadata::getTagGroupId)
                .toList();

        List<Long> tagIds = storageMetadata.stream()
                .map(StorageMetadata::getTagId)
                .toList();
        List<TagGroupInfo> tagGroupInfos =
                contentTagProvider.getTagGroupInfos(tagGroupIds);
        List<ContentTagInfo> tags =
                contentTagProvider.getTags(tagIds);
        return pairWithTags(tagGroupInfos, tags);
    }

    private List<TaggedValue> pairWithTags(List<TagGroupInfo> tagGroupInfos,
                                           List<ContentTagInfo> tags) {
        List<TaggedValue> tagValues = new ArrayList<>();
        List<ContentTagInfo> sortedTags = tags.stream()
                .sorted(Comparator.comparingLong(ContentTagInfo::id))
                .toList();
        for (TagGroupInfo tagGroupInfo : tagGroupInfos) {
            ContentTagInfo tag = findInTags(sortedTags, tagGroupInfo.tags());
            tagValues.add(new TaggedValue(
                    tagGroupInfo.id(),
                    tag.id(),
                    tagGroupInfo.name(),
                    tag.name()
            ));
        }
        return tagValues;
    }

    private ContentTagInfo findInTags(List<ContentTagInfo> tags,
                                      long[] tagIds) {
        return tags.stream()
                .filter(tag -> Arrays.stream(tagIds).anyMatch(tagId -> tag.id() == tagId))
                .findFirst()
                .orElse(null);
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
