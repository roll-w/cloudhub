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

package org.cloudhub.meta.server.service.synchro;

import io.grpc.stub.StreamObserver;
import org.cloudhub.file.rpc.synchro.SynchroRequest;
import org.cloudhub.file.rpc.synchro.SynchroResponse;
import org.cloudhub.file.rpc.synchro.SynchroServiceGrpc;
import org.cloudhub.meta.server.service.node.*;
import org.cloudhub.rpc.GrpcServiceStubPool;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 同步服务
 *
 * @author RollW
 */
@Service
public class SynchroDispatchService {
    private static final Logger logger = LoggerFactory.getLogger(SynchroDispatchService.class);

    private final NodeChannelPool nodeChannelPool;
    private final GrpcServiceStubPool<SynchroServiceGrpc.SynchroServiceStub>
            synchroServiceStubPool;
    private final NodeAllocator nodeAllocator;
    private final ServerChecker serverChecker;

    public SynchroDispatchService(NodeChannelPool nodeChannelPool,
                                  HeartbeatService heartbeatService) {
        this.nodeChannelPool = nodeChannelPool;
        this.synchroServiceStubPool = new GrpcServiceStubPool<>();
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.serverChecker = heartbeatService.getServerChecker();
    }

    // tell the server, needs synchro which part of the data
    public void dispatchSynchro(NodeServer fileServer,
                                Collection<String> fileParts,
                                List<NodeServer> destServers) {
        List<SerializedFileServer> serializedServers =
                destServers.stream()
                        .map((server) ->
                                SerializedFileServer.newBuilder()
                                        .setId(server.getId())
                                        .setHost(server.host())
                                        .setPort(server.port())
                                        .build())
                        .toList();
        // need reduces the fileParts, or let the file-server to do it
        SynchroRequest request = SynchroRequest.newBuilder()
                .addAllFileIds(fileParts)
                .addAllServers(serializedServers)
                .build();
        if (!serverChecker.isActive(fileServer)) {
            logger.error("Synchro failed, server not active, serverId: {}",
                    fileServer.getId());
            throw new NodeServerException(
                    "Synchro failed, server not active, serverId: " +
                            fileServer.getId()
            );
        }

        SynchroServiceGrpc.SynchroServiceStub synchroServiceStub =
                getOrCreateStub(fileServer);
        synchroServiceStub.sendSynchro(request, new SynchroResponseObserver(fileServer));
    }

    private SynchroServiceGrpc.SynchroServiceStub getOrCreateStub(NodeServer server) {
        return synchroServiceStubPool.getStub(
                server.id(),
                () -> SynchroServiceGrpc.newStub(nodeChannelPool.getChannel(server))
        );
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class SynchroResponseObserver
            implements StreamObserver<SynchroResponse> {
        private final NodeServer server;

        public SynchroResponseObserver(NodeServer server) {
            this.server = server;
        }

        @Override
        public void onNext(SynchroResponse value) {
            // do nothing
        }

        @Override
        public void onError(Throwable t) {
            logger.error("Synchro failed, server: {}", server, t);
        }

        @Override
        public void onCompleted() {
            // do nothing
        }
    }

}
