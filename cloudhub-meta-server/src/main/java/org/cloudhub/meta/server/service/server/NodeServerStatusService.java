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
import org.cloudhub.client.rpc.server.FileServerStatusRequest;
import org.cloudhub.client.rpc.server.FileServerStatusResponse;
import org.cloudhub.client.rpc.server.FileServerStatusServiceGrpc;
import org.cloudhub.meta.server.service.file.RequestServer;
import org.cloudhub.meta.server.service.node.HeartbeatService;
import org.cloudhub.meta.server.service.node.ServerChecker;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class NodeServerStatusService extends FileServerStatusServiceGrpc.FileServerStatusServiceImplBase {
    private final ServerChecker serverChecker;

    public NodeServerStatusService(HeartbeatService heartbeatService) {
        this.serverChecker = heartbeatService.getServerChecker();
    }

    @Override
    public void requestServers(FileServerStatusRequest request, StreamObserver<FileServerStatusResponse> responseObserver)  {
        List<SerializedFileServer> activeServers =
                RequestServer.toSerialized(serverChecker.getActiveServers());
        List<SerializedFileServer> deadServers =
                RequestServer.toSerialized(serverChecker.getDeadServers());

        FileServerStatusResponse response = FileServerStatusResponse.newBuilder()
                .addAllActiveServers(activeServers)
                .addAllDeadServers(deadServers)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
