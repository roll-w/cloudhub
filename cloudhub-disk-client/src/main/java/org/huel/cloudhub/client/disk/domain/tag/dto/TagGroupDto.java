package org.huel.cloudhub.client.disk.domain.tag.dto;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;

import java.util.List;

/**
 * @author RollW
 */
public record TagGroupDto(
        long id,
        long parent,
        String name,
        String description,
        List<ContentTagInfo> tags,
        KeywordSearchScope keywordSearchScope,
        List<TagGroupDto> children,
        long createTime,
        long updateTime
) {

    public static TagGroupDto of(TagGroup tagGroup, List<ContentTagInfo> tags) {
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

    public static TagGroupDto of(TagGroup tagGroup) {
        return TagGroupDto.of(tagGroup, List.of());
    }

    public ContentTagInfo findByName(String name) {
        return tags.stream()
                .filter(tag -> tag.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
