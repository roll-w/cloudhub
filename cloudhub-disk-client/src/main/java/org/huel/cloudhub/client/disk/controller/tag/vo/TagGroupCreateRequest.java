package org.huel.cloudhub.client.disk.controller.tag.vo;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;

/**
 * @author RollW
 */
public record TagGroupCreateRequest(
        Long parent,
        String name,
        String description,
        KeywordSearchScope keywordSearchScope
) {
}
