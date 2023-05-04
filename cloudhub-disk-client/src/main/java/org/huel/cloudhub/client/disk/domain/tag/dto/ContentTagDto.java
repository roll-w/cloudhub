package org.huel.cloudhub.client.disk.domain.tag.dto;

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
}
