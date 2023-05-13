package org.huel.cloudhub.web;

/**
 * @author RollW
 */
public record RequestMetadata(
        String ip,
        String userAgent,
        long timestamp
) {
}
