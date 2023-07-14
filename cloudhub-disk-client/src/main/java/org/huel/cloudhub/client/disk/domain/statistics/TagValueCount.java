package org.huel.cloudhub.client.disk.domain.statistics;

/**
 * @author RollW
 */
public record TagValueCount(
        long tagId,
        String name,
        long count
) {
}
