package org.huel.cloudhub.client.disk.controller.tag.vo;

import org.huel.cloudhub.client.disk.domain.tag.KeywordSearchScope;
import org.huel.cloudhub.client.disk.domain.tag.dto.TagGroupDto;

/**
 * @author RollW
 */
public record TagGroupVo(
        long id,
        long parent,
        String name,
        String description,
        KeywordSearchScope keywordSearchScope,
        long createTime,
        long updateTime
) {

    public static TagGroupVo from(TagGroupDto tagGroupDto) {
        return new TagGroupVo(
                tagGroupDto.id(),
                tagGroupDto.parent(),
                tagGroupDto.name(),
                tagGroupDto.description(),
                tagGroupDto.keywordSearchScope(),
                tagGroupDto.createTime(),
                tagGroupDto.updateTime()
        );
    }

}
