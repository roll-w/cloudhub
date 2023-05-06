package org.huel.cloudhub.client.disk.domain.tag.service;

import org.cloudhub.util.Keywords;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.huel.cloudhub.client.disk.domain.tag.ContentTagService;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;
import org.huel.cloudhub.client.disk.domain.tag.TagKeyword;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagDto;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagGroupInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.ContentTagInfo;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;
import org.huel.cloudhub.client.disk.domain.tag.repository.ContentTagRepository;
import org.huel.cloudhub.client.disk.domain.tag.repository.TagGroupRepository;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class ContentTagServiceImpl implements ContentTagService {
    private final ContentTagRepository contentTagRepository;
    private final TagGroupRepository tagGroupRepository;

    public ContentTagServiceImpl(ContentTagRepository contentTagRepository,
                                 TagGroupRepository tagGroupRepository) {
        this.contentTagRepository = contentTagRepository;
        this.tagGroupRepository = tagGroupRepository;
    }

    @Override
    public ContentTagDto getTagById(long id) {
        ContentTag contentTag =
                contentTagRepository.getById(id);
        return ContentTagDto.of(contentTag);
    }

    @Override
    public TagGroupDto getTagGroupById(long id) {
        TagGroup tagGroup =
                tagGroupRepository.getById(id);
        if (tagGroup == null) {
            return null;
        }
        List<ContentTag> contentTags =
                contentTagRepository.getTagsBy(tagGroup.getTags());
        return pairWith(tagGroup, contentTags);
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
    public List<ContentTagDto> getTags(Pageable pageable) {
        return contentTagRepository.get(pageable.toOffset())
                .stream()
                .map(ContentTagDto::of)
                .toList();
    }

    @Override
    public ContentTagDto getByName(String name) {
        return null;
        // return ContentTagDto.of(contentTagRepository.getByName(name));
    }

    @Override
    public void createContentTagGroup(ContentTagGroupInfo tagGroupInfo) {
        long now = System.currentTimeMillis();
        TagGroup tagGroup = TagGroup.builder()
                .setTags(new long[0])
                .setName(tagGroupInfo.name())
                .setDescription(tagGroupInfo.description())
                .setKeywordSearchScope(tagGroupInfo.keywordSearchScope())
                .setParentId(tagGroupInfo.parent())
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        long id = tagGroupRepository.insert(tagGroup);
        TagGroup inserted = tagGroup
                .toBuilder()
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .setChangedContent(inserted.getName());
    }

    @Override
    public void createContentTag(ContentTagInfo contentTagInfo) {
        long now = System.currentTimeMillis();
        ContentTag contentTag = ContentTag.builder()
                .setName(contentTagInfo.name())
                .setKeywords(contentTagInfo.keywords())
                .setDescription(contentTagInfo.description())
                .setCreateTime(now)
                .setUpdateTime(now)
                .build();
        long id = contentTagRepository.insert(contentTag);
        ContentTag inserted = contentTag
                .toBuilder()
                .setId(id)
                .build();
        OperationContextHolder.getContext()
                .addSystemResource(inserted)
                .setChangedContent(inserted.getName());
    }

    @Override
    public void importFromKeywordsFile(InputStream stream, long tagGroupId) {
        Keywords keywords = new Keywords(stream);
        List<Keywords.KeywordsGroup> keywordsGroups =
                keywords.listGroups();
        if (keywordsGroups == null || keywordsGroups.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        List<ContentTag> contentTags = new ArrayList<>();
        for (Keywords.KeywordsGroup keywordsGroup : keywordsGroups) {
            ContentTag contentTag = createFrom(keywordsGroup, now);
            contentTags.add(contentTag);
        }
        contentTagRepository.insert(contentTags);
    }

    private ContentTag createFrom(Keywords.KeywordsGroup keywordsGroup, long time) {
        String name = keywordsGroup.name();
        return ContentTag
                .builder()
                .setName(name)
                .setKeywords(convertsTo(keywordsGroup.keywords()))
                .setCreateTime(time)
                .setUpdateTime(time)
                .setDeleted(false)
                .build();
    }

    private List<TagKeyword> convertsTo(List<Keywords.Keyword> keywords) {
        List<TagKeyword> tagKeywords = new ArrayList<>();
        for (Keywords.Keyword keyword : keywords) {
            tagKeywords.add(new TagKeyword(
                    keyword.word(),
                    keyword.weight()
            ));
        }
        return tagKeywords;
    }

}
