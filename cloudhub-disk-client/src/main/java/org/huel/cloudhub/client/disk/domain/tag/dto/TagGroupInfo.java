package org.huel.cloudhub.client.disk.domain.tag.dto;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;
import org.huel.cloudhub.client.disk.domain.tag.TagGroup;

/**
 * @author RollW
 */
public record TagGroupInfo(
        long id,
        String name,
        String description,
        KeywordSearchScope keywordSearchScope,
        long[] tags,
        long createTime,
        long updateTime
) {
    public static TagGroupInfo of(TagGroup tagGroup) {
        return new TagGroupInfo(
                tagGroup.getId(),
                tagGroup.getName(),
                tagGroup.getDescription(),
                tagGroup.getKeywordSearchScope(),
                tagGroup.getTags(),
                tagGroup.getCreateTime(),
                tagGroup.getUpdateTime()
        );
    }
}
