package org.huel.cloudhub.file.server.service.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.diagnosis.Diagnosable;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatServiceGrpc;
import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.huel.cloudhub.server.rpc.status.SerializedServerStatus;
import org.huel.cloudhub.server.rpc.status.SerializedServerStatusCode;

/**
 * @author RollW
 */
public class HeartbeatSender {
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Diagnosable<SerializedDamagedContainerReport> damagedContainerDiagnosis;
    private final SourceServerGetter.ServerInfo serverInfo;

    public HeartbeatSender(ManagedChannel channel,
                           Diagnosable<SerializedDamagedContainerReport> damagedContainerDiagnosis,
                           SourceServerGetter sourceServerGetter) {
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
        this.damagedContainerDiagnosis = damagedContainerDiagnosis;
        this.serverInfo = sourceServerGetter.getLocalServer();
    }

    private HeartbeatResponse lastResponse;

    public HeartbeatResponse sendHeartbeat() {
        boolean sendStat = lastResponse != null &&
                lastResponse.hasContainsStatNext() && lastResponse.getContainsStatNext();
        // TODO: status, every 5 or 10 heartbeats send once status?
        SerializedServerStatus status = SerializedServerStatus.newBuilder()
                .build();
        Heartbeat heartbeat = Heartbeat.newBuilder()
                .setHost(serverInfo.host())
                .setPort(serverInfo.port())
                .setId(serverInfo.id())
                .setStatusCode(SerializedServerStatusCode.HEALTHY)
                .build();
        damagedContainerDiagnosis.getDiagnosisReport();
        HeartbeatResponse resp = serviceStub.receiveHeartbeat(heartbeat);
        lastResponse = resp;
        return resp;
    }
}
