package org.huel.cloudhub.client.disk.domain.cfsserver;

import org.huel.cloudhub.client.CFSStatus;

/**
 * @author RollW
 */
public record ServerStatusSummary(
        long runtime,
        CFSStatus cfsStatus,
        int activeFileServers,
        int deadFileServers,
        UsageInfo diskUsage,
        UsageInfo memoryUsage
) {

}
