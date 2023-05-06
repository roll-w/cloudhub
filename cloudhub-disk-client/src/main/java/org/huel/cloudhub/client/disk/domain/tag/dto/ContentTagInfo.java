package org.huel.cloudhub.client.disk.domain.tag.dto;

import org.huel.cloudhub.client.disk.domain.tag.TagKeyword;

import java.util.List;

/**
 * @author RollW
 */
public record ContentTagInfo(
        String name,
        String description,
        List<TagKeyword> keywords
) {
}
