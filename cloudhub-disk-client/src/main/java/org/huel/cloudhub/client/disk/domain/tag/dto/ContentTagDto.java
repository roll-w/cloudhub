package org.huel.cloudhub.client.disk.domain.tag.dto;

import org.huel.cloudhub.client.disk.domain.tag.ContentTag;
import org.huel.cloudhub.client.disk.domain.tag.TagKeyword;

import java.util.List;

/**
 * @author RollW
 */
public record ContentTagDto(
        long id,
        String name,
        List<TagKeyword> keywords,
        String description,
        long createTime,
        long updateTime
) {
    public TagKeyword findKeywordByName(String name) {
        return keywords.stream()
                .filter(keyword -> keyword.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static ContentTagDto of(ContentTag contentTag) {
        if (contentTag == null) {
            return null;
        }

        return new ContentTagDto(
                contentTag.getId(),
                contentTag.getName(),
                contentTag.getKeywords(),
                contentTag.getDescription(),
                contentTag.getCreateTime(),
                contentTag.getUpdateTime()
        );
    }
}
