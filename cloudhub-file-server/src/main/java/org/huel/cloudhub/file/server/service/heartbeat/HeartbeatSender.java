package org.huel.cloudhub.file.server.service.heartbeat;

import io.grpc.ManagedChannel;
import org.huel.cloudhub.file.diagnosis.Diagnosable;
import org.huel.cloudhub.file.diagnosis.DiagnosisReportSegment;
import org.huel.cloudhub.file.server.service.Monitorable;
import org.huel.cloudhub.file.server.service.SourceServerGetter;
import org.huel.cloudhub.fs.status.StatusKeys;
import org.huel.cloudhub.server.DiskUsageInfo;
import org.huel.cloudhub.server.ServerHostInfo;
import org.huel.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.huel.cloudhub.server.rpc.heartbeat.HeartbeatServiceGrpc;
import org.huel.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.huel.cloudhub.server.rpc.status.SerializedServerStatusReport;
import org.huel.cloudhub.server.rpc.status.SerializedServerStatusCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public class HeartbeatSender {
    private final HeartbeatServiceGrpc.HeartbeatServiceBlockingStub serviceStub;
    private final Diagnosable<SerializedDamagedContainerReport> damagedContainerDiagnosis;
    private final SourceServerGetter.ServerInfo serverInfo;
    private final Monitorable monitorable;

    public HeartbeatSender(ManagedChannel channel,
                           Diagnosable<SerializedDamagedContainerReport> damagedContainerDiagnosis,
                           SourceServerGetter sourceServerGetter,
                           Monitorable monitorable) {
        this.serviceStub = HeartbeatServiceGrpc.newBlockingStub(channel);
        this.damagedContainerDiagnosis = damagedContainerDiagnosis;
        this.serverInfo = sourceServerGetter.getLocalServer();
        this.monitorable = monitorable;
    }

    private HeartbeatResponse lastResponse;

    public HeartbeatResponse sendHeartbeat() {
        boolean sendStat = lastResponse == null ||
                lastResponse.hasContainsStatNext() && lastResponse.getContainsStatNext();

        Heartbeat.Builder heartbeatBuilder = Heartbeat.newBuilder()
                .setHost(serverInfo.host())
                .setPort(serverInfo.port())
                .setId(serverInfo.id())
                .setStatusCode(SerializedServerStatusCode.HEALTHY);
        if (sendStat) {
            List<SerializedDamagedContainerReport> reports = damagedContainerDiagnosis.getDiagnosisReport()
                    .getSegments()
                    .stream()
                    .map(DiagnosisReportSegment::getData)
                    .toList();
            Map<String, String> status = buildStatus();
            SerializedServerStatusReport statusReport = SerializedServerStatusReport.newBuilder()
                    .addAllReport(reports)
                    .putAllStatus(status)
                    .build();
            heartbeatBuilder.setStatus(statusReport);
        }

        HeartbeatResponse resp = serviceStub.receiveHeartbeat(heartbeatBuilder.build());
        lastResponse = resp;
        return resp;
    }

    private Map<String, String> buildStatus() {
        Map<String, String> status = new HashMap<>();
        ServerHostInfo serverHostInfo = monitorable.getMonitor().getLatest();
        if (serverHostInfo == null) {
            return status;
        }

        DiskUsageInfo diskUsageInfo = serverHostInfo.getDiskUsageInfo();
        status.put(StatusKeys.STORAGE_REMAINING, String.valueOf(diskUsageInfo.getFree()));

        return status;
    }
}
