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
        List<ContentTagDto> tags,
        KeywordSearchScope keywordSearchScope,
        List<TagGroupDto> children,
        long createTime,
        long updateTime
) {

    public static TagGroupDto of(TagGroup inserted) {
        return new TagGroupDto(
                inserted.getId(),
                inserted.getParentId() == null ? 0 : inserted.getParentId(),
                inserted.getName(),
                inserted.getDescription(),
                List.of(),
                inserted.getKeywordSearchScope(),
                null,
                inserted.getCreateTime(),
                inserted.getUpdateTime()
        );
    }

    public ContentTagDto findByName(String name) {
        return tags.stream()
                .filter(tag -> tag.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
