package org.huel.cloudhub.meta.server.service.node;

import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;

import java.util.List;

/**
 * @author RollW
 */
public record ServerContainerStatus(
        String serverId,
        List<SerializedDamagedContainerReport> damagedContainerReports
) {
}
