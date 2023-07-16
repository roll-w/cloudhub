package org.huel.cloudhub.client.disk.controller.tag.vo;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;

/**
 * @author RollW
 */
public record TagGroupUpdateRequest(
        String name,
        String description,
        KeywordSearchScope keywordSearchScope
) {
}
