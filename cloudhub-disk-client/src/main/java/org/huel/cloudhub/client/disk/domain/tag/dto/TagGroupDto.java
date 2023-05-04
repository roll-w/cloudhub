package org.huel.cloudhub.client.disk.domain.tag.dto;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;

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

    public ContentTagDto findByName(String name) {
        return tags.stream()
                .filter(tag -> tag.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
