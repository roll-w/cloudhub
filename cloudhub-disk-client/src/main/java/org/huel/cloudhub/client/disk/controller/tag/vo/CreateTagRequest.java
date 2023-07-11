package org.huel.cloudhub.client.disk.controller.tag.vo;

import org.huel.cloudhub.client.disk.domain.tag.TagKeyword;

import java.util.List;

/**
 * @author RollW
 */
public record CreateTagRequest(
        String name,
        String description,
        List<TagKeyword> keywords
) {
}
