/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.file.server.service.heartbeat;

import io.grpc.ManagedChannel;
import org.cloudhub.file.diagnosis.Diagnosable;
import org.cloudhub.file.diagnosis.DiagnosisReportSegment;
import org.cloudhub.file.server.service.Monitorable;
import org.cloudhub.file.server.service.SourceServerGetter;
import org.cloudhub.fs.status.StatusKeys;
import org.cloudhub.server.DiskUsageInfo;
import org.cloudhub.server.ServerHostInfo;
import org.cloudhub.server.rpc.heartbeat.Heartbeat;
import org.cloudhub.server.rpc.heartbeat.HeartbeatResponse;
import org.cloudhub.server.rpc.heartbeat.HeartbeatServiceGrpc;
import org.cloudhub.server.rpc.status.SerializedDamagedContainerReport;
import org.cloudhub.server.rpc.status.SerializedServerStatusCode;
import org.cloudhub.server.rpc.status.SerializedServerStatusReport;

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
