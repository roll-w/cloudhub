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

package org.cloudhub.file.server.service.server;

import io.grpc.stub.StreamObserver;
import org.cloudhub.file.fs.LocalFileServer;
import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.file.server.service.Monitorable;
import org.cloudhub.server.ServerHostInfo;
import org.cloudhub.server.ServerInfoSerializeHelper;
import org.cloudhub.server.ServerStatusMonitor;
import org.cloudhub.server.rpc.server.SerializedDiskUsageInfo;
import org.cloudhub.server.rpc.server.SerializedNetworkUsageInfo;
import org.cloudhub.file.fs.LocalFileServer;
import org.cloudhub.file.fs.ServerFile;
import org.cloudhub.file.fs.container.ContainerProperties;
import org.cloudhub.file.server.service.Monitorable;
import org.cloudhub.server.ServerHostInfo;
import org.cloudhub.server.ServerInfoSerializeHelper;
import org.cloudhub.server.ServerStatusMonitor;
import org.cloudhub.server.rpc.server.SerializedDiskUsageInfo;
import org.cloudhub.server.rpc.server.SerializedNetworkUsageInfo;
import org.cloudhub.server.rpc.server.SerializedServerStatus;
import org.cloudhub.server.rpc.server.ServerDiskRecordResponse;
import org.cloudhub.server.rpc.server.ServerNetworkRecordResponse;
import org.cloudhub.server.rpc.server.ServerStatusRecordRequest;
import org.cloudhub.server.rpc.server.ServerStatusRecordResponse;
import org.cloudhub.server.rpc.server.ServerStatusRequest;
import org.cloudhub.server.rpc.server.ServerStatusResponse;
import org.cloudhub.server.rpc.server.ServerStatusServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileServerStatusService
        extends ServerStatusServiceGrpc.ServerStatusServiceImplBase
        implements Monitorable {
    private final ServerStatusMonitor serverStatusMonitor;

    public FileServerStatusService(ContainerProperties containerProperties,
                                   LocalFileServer localFileServer) {
        ServerFile file = localFileServer.getServerFileProvider()
                .openFile(containerProperties.getFilePath());
        this.serverStatusMonitor = new ServerStatusMonitor(file.getPath());
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

    @Override
    public ServerStatusMonitor getMonitor() {
        return serverStatusMonitor;
    }
}
