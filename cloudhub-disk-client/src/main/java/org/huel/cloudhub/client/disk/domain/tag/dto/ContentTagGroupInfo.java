package org.huel.cloudhub.client.disk.domain.tag.dto;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;

/**
 * @author RollW
 */
public record ContentTagGroupInfo(
        Long parent,
        String name,
        String description,
        KeywordSearchScope keywordSearchScope
) {
}
