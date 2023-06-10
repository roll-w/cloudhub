package org.huel.cloudhub.client.disk.domain.userstats.dto;

/**
 * @author RollW
 */
public record RestrictInfo(
        String key,
        long value,
        long restrict
) {
}
