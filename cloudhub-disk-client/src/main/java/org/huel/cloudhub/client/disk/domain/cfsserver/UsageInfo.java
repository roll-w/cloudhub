package org.huel.cloudhub.client.disk.domain.cfsserver;

/**
 * @author RollW
 */
public record UsageInfo(
        long total,
        long used
) {
}
