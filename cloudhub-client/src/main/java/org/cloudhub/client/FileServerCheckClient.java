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

package org.cloudhub.client;

import io.grpc.ManagedChannel;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.cloudhub.client.server.ConnectedServers;
import org.cloudhub.client.server.ContainerStatus;
import org.cloudhub.client.server.FileServerLocation;
import org.cloudhub.client.rpc.server.FileServerStatusRequest;
import org.cloudhub.client.rpc.server.FileServerStatusResponse;
import org.cloudhub.client.rpc.server.FileServerStatusServiceGrpc;
import org.cloudhub.file.rpc.container.ContainerStatusRequest;
import org.cloudhub.file.rpc.container.ContainerStatusResponse;
import org.cloudhub.file.rpc.container.ContainerStatusServiceGrpc;
import org.cloudhub.rpc.GrpcServiceStubPool;
import org.cloudhub.server.rpc.server.SerializedFileServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public class FileServerCheckClient {
    private final FileServerStatusServiceGrpc.FileServerStatusServiceBlockingStub stub;
    private volatile FileServerStatusResponse lastResp;
    private final FileServerChannelPool fileServerChannelPool;
    private final GrpcServiceStubPool<ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub>
            containerStatusStubPool;

    public FileServerCheckClient(MetaServerConnection metaServerConnection,
                                 FileServerChannelPool fileServerChannelPool) {
        this.stub = FileServerStatusServiceGrpc.newBlockingStub(metaServerConnection.getManagedChannel());
        this.fileServerChannelPool = fileServerChannelPool;
        this.containerStatusStubPool = new GrpcServiceStubPool<>();
    }

    public ConnectedServers getConnectedServers() {
        lastResp = stub.requestServers(
                FileServerStatusRequest.newBuilder().build());
        List<FileServerLocation> actives = deserialize(lastResp.getActiveServersList());
        List<FileServerLocation> deads = deserialize(lastResp.getDeadServersList());
        return new ConnectedServers(actives, deads);
    }

    @Nullable
    public List<ContainerStatus> getContainerStatuses(String id) {
        if (id == null) {
            return null;
        }
        getConnectedServers();
        List<ContainerStatus> res = trySendContainerRequest(lastResp.getActiveServersList(), id);
        if (res != null) {
            return res;
        }
        return trySendContainerRequest(lastResp.getDeadServersList(), id);
    }

    private List<ContainerStatus> trySendContainerRequest(List<SerializedFileServer> servers, String id) {
        for (SerializedFileServer server : servers) {
            if (!server.getId().equals(id)) {
                continue;
            }
            ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub
                    stub = requireContainerStatusStub(server);
            ContainerStatusResponse response =
                    stub.getContainerInfo(ContainerStatusRequest.newBuilder()
                    .build());
            return response.getInfosList().stream()
                    .map(ContainerStatus::deserialize).toList();
        }
        return null;
    }

    private ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub requireContainerStatusStub(SerializedFileServer server) {
        ContainerStatusServiceGrpc.ContainerStatusServiceBlockingStub stub =
                containerStatusStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        ManagedChannel channel = fileServerChannelPool.getChannel(server);
        stub = ContainerStatusServiceGrpc.newBlockingStub(channel);
        containerStatusStubPool.registerStub(server.getId(), stub);
        return stub;
    }

    private static List<FileServerLocation> deserialize(List<SerializedFileServer> servers) {
        return servers.stream()
                .map(serializedFileServer -> new FileServerLocation(
                        serializedFileServer.getHost(),
                        serializedFileServer.getPort(),
                        serializedFileServer.getId()))
                .toList();
    }

    public List<SerializedFileServer> getAllServers() {
        if (lastResp == null) {
            getConnectedServers();
        }

        List<SerializedFileServer> servers =
                new ArrayList<>(lastResp.getActiveServersList());
        servers.addAll(lastResp.getDeadServersList());
        return servers;
    }
}
