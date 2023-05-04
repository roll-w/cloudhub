package org.huel.cloudhub.client.disk.domain.tag.dto;

/**
 * @author RollW
 */
public record ContentTagDto(
        long id,
        String name,
        String description,
        long createTime,
        long updateTime
) {
}
