package org.huel.cloudhub.client.disk.domain.tag.dto;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;

import java.util.List;

/**
 * @author RollW
 */
public record TagGroupDto(
        long id,
        long parent,
        List<ContentTagDto> tags,
        KeywordSearchScope keywordSearchScope,
        List<TagGroupDto> children,
        long createTime,
        long updateTime
) {
}
