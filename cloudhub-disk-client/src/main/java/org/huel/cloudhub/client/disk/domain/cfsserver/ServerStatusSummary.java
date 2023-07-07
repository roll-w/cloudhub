package org.huel.cloudhub.client.disk.domain.cfsserver;

/**
 * @author RollW
 */
public record ServerStatusSummary(
        long runtime,
        int activeFileServers,
        int deadFileServers
) {
}
