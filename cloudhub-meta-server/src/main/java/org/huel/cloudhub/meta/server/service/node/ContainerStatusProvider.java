package org.huel.cloudhub.meta.server.service.node;

import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;

import java.util.List;

/**
 * @author RollW
 */
public interface ContainerStatusProvider {
    List<SerializedDamagedContainerReport> getDamagedContainerReports(NodeServer nodeServer);

    List<ServerContainerStatus> getDamagedContainerReports();

    // TODO: replace SerializedDamagedContainerReport with other class
}
