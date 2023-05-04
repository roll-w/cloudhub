package org.huel.cloudhub.client.disk.domain.storageprocess.service;

import org.cloudhub.util.Keywords;
import org.cloudhub.util.KeywordsScorer;
import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.huel.cloudhub.client.disk.domain.tag.TagKeyword;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagDto;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.client.disk.domain.tag.repository.ContentTagRepository;
import org.huel.cloudhub.client.disk.domain.tag.repository.TagGroupRepository;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageMetadata;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageProcessor;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.StorageAttr;
import org.huel.cloudhub.client.disk.domain.userstorage.repository.StorageMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class FileTagProcessServiceImpl implements StorageProcessor {
    private final StorageMetadataRepository storageMetadataRepository;
    private final ContentTagRepository contentTagRepository;
    private final TagGroupRepository tagGroupRepository;

    private final List<KeywordProcessor> keywordProcessors = new ArrayList<>();

    public FileTagProcessServiceImpl(StorageMetadataRepository storageMetadataRepository,
                                     ContentTagRepository contentTagRepository,
                                     TagGroupRepository tagGroupRepository) {
        this.storageMetadataRepository = storageMetadataRepository;
        this.contentTagRepository = contentTagRepository;
        this.tagGroupRepository = tagGroupRepository;

        loadKeywordsGroup();
    }


    private void loadKeywordsGroup() {
        List<TagGroup> tagGroups = tagGroupRepository.get();
        List<ContentTag> contentTags = contentTagRepository.get();

        List<TagGroupDto> tagGroupDtos = matchWith(tagGroups, contentTags);
        for (TagGroupDto tagGroupDto : tagGroupDtos) {
            Keywords keywords = new Keywords(mapToKeywordMap(tagGroupDto));
            KeywordsScorer keywordsScorer = new KeywordsScorer(keywords);
            KeywordProcessor keywordProcessor = new KeywordProcessor(
                    keywords,
                    keywordsScorer,
                    tagGroupDto.keywordSearchScope(),
                    tagGroupDto
            );
            keywordProcessors.add(keywordProcessor);
        }
    }

    private Map<String, List<Keywords.Keyword>> mapToKeywordMap(TagGroupDto tagGroupDto) {
        List<ContentTagDto> contentTagDtos = tagGroupDto.tags();
        Map<String, List<Keywords.Keyword>> map = new HashMap<>();
        for (ContentTagDto contentTagDto : contentTagDtos) {
            List<Keywords.Keyword> keywords = new ArrayList<>();
            for (TagKeyword keyword : contentTagDto.keywords()) {
                keywords.add(new Keywords.Keyword(
                        keyword.name(),
                        keyword.weight())
                );
            }
            map.put(contentTagDto.name(), keywords);
        }
        return map;
    }

    private List<TagGroupDto> matchWith(List<TagGroup> tagGroups,
                                        List<ContentTag> contentTags) {
        List<TagGroupDto> tagGroupDtos = new ArrayList<>();
        for (TagGroup tagGroup : tagGroups) {
            tagGroupDtos.add(pairWith(tagGroup, contentTags));
        }
        return tagGroupDtos;
    }


    private TagGroupDto pairWith(TagGroup tagGroup,
                                 List<ContentTag> contentTags) {
        long[] tagIds = tagGroup.getTags();
        List<ContentTagDto> tags = new ArrayList<>();
        for (long tagId : tagIds) {
            for (ContentTag contentTag : contentTags) {
                if (contentTag.getId() == tagId) {
                    tags.add(new ContentTagDto(
                            contentTag.getId(),
                            contentTag.getName(),
                            contentTag.getKeywords(),
                            contentTag.getDescription(),
                            contentTag.getCreateTime(),
                            contentTag.getUpdateTime())
                    );
                }
            }
        }
        return new TagGroupDto(
                tagGroup.getId(),
                tagGroup.getParentId() == null ? 0 : tagGroup.getParentId(),
                tagGroup.getName(),
                tagGroup.getDescription(),
                tags,
                tagGroup.getKeywordSearchScope(),
                List.of(),
                tagGroup.getCreateTime(),
                tagGroup.getUpdateTime()
        );
    }

    @Override
    public void onStorageCreated(Storage storage,
                                 StorageAttr storageAttr) {
        String name = storage.getName();
        for (KeywordProcessor keywordProcessor : keywordProcessors) {
            if (!allowName(keywordProcessor.searchScope())) {
                continue;
            }
            KeywordsScorer scorer = keywordProcessor.scorer();
            List<KeywordsScorer.Rank> ranks = scorer.score(name);
            if (ranks.isEmpty()) {
                continue;
            }
            buildMetadata(storage, keywordProcessor, ranks.get(0));
        }

    }

    private void buildMetadata(Storage storage, KeywordProcessor keywordProcessor,
                               KeywordsScorer.Rank rank) {
        TagGroupDto tagGroupDto = keywordProcessor.tagGroupDto();
        ContentTagDto contentTagDto = tagGroupDto.findByName(rank.getGroup());
        long time = System.currentTimeMillis();
        StorageMetadata exist = storageMetadataRepository.getByStorageIdAndName(
                storage.getStorageId(), tagGroupDto.name()
        );
        if (exist != null) {
            StorageMetadata updated = exist.toBuilder()
                    .setValue(rank.getGroup())
                    .setUpdateTime(time)
                    .build();
            storageMetadataRepository.update(updated);
            return;
        }

        StorageMetadata storageMetadata = StorageMetadata.builder()
                .setTagId(contentTagDto.id())
                .setTagGroupId(tagGroupDto.id())
                .setName(tagGroupDto.name())
                .setValue(rank.getGroup())
                .setCreateTime(time)
                .setUpdateTime(time)
                .setStorageId(storage.getStorageId())
                .build();
        storageMetadataRepository.insert(storageMetadata);
    }

    private boolean allowName(KeywordSearchScope searchScope) {
        if (searchScope == KeywordSearchScope.NAME) {
            return true;
        }
        return searchScope == KeywordSearchScope.ALL;
    }

    private record KeywordProcessor(
            Keywords keywords,
            KeywordsScorer scorer,
            KeywordSearchScope searchScope,
            TagGroupDto tagGroupDto
    ) {
    }
}
