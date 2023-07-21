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

package org.cloudhub.meta.server.service.server;

import io.grpc.stub.StreamObserver;
import org.cloudhub.meta.server.configuration.FileProperties;
import org.cloudhub.server.ServerHostInfo;
import org.cloudhub.server.ServerInfoSerializeHelper;
import org.cloudhub.server.ServerStatusMonitor;
import org.cloudhub.server.rpc.server.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class MetaServerStatusService extends ServerStatusServiceGrpc.ServerStatusServiceImplBase {
    private final ServerStatusMonitor serverStatusMonitor;

    public MetaServerStatusService(FileProperties fileProperties) {
        File file = new File(fileProperties.getDataPath());
        this.serverStatusMonitor = new ServerStatusMonitor(file.getAbsolutePath());
        this.serverStatusMonitor.setLimit(100);
        this.serverStatusMonitor.setRecordFrequency(1000);
        this.serverStatusMonitor.startMonitor();
    }

    @Override
    public void requestServerStatus(ServerStatusRequest request, StreamObserver<ServerStatusResponse> responseObserver) {
        ServerHostInfo info = serverStatusMonitor.getLatest();
        responseObserver.onNext(ServerStatusResponse.newBuilder()
                .setStatus(ServerInfoSerializeHelper.serializeFrom(info))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestServerRecordStatuses(ServerStatusRecordRequest request,
                                            StreamObserver<ServerStatusRecordResponse> responseObserver) {
        int count = request.getRecordNum();
        List<SerializedServerStatus> serverStatuses = serverStatusMonitor
                .getRecent(count)
                .stream()
                .map(ServerInfoSerializeHelper::serializeFrom)
                .toList();
        responseObserver.onNext(ServerStatusRecordResponse.newBuilder()
                .addAllStatues(serverStatuses)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestServerNetworkRecord(ServerStatusRecordRequest request, StreamObserver<ServerNetworkRecordResponse> responseObserver) {
        int count = request.getRecordNum();
        List<SerializedNetworkUsageInfo> netInfos = serverStatusMonitor
                .getRecent(count)
                .stream()
                .map(ServerInfoSerializeHelper::serializeFrom)
                .map(SerializedServerStatus::getNetInfo)
                .toList();
        responseObserver.onNext(ServerNetworkRecordResponse.newBuilder()
                .addAllNets(netInfos)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestServerDiskRecord(ServerStatusRecordRequest request, StreamObserver<ServerDiskRecordResponse> responseObserver) {
        int count = request.getRecordNum();
        List<SerializedDiskUsageInfo> diskInfos = serverStatusMonitor
                .getRecent(count)
                .stream()
                .map(ServerInfoSerializeHelper::serializeFrom)
                .map(SerializedServerStatus::getDiskInfo)
                .toList();
        responseObserver.onNext(ServerDiskRecordResponse.newBuilder()
                .addAllDisks(diskInfos)
                .build());
        responseObserver.onCompleted();
    }
}
