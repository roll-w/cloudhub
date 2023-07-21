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

package org.cloudhub.meta.server.service.file;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.cloudhub.file.rpc.block.BlockDeleteServiceGrpc;
import org.cloudhub.file.rpc.block.DeleteBlocksRequest;
import org.cloudhub.file.rpc.block.DeleteBlocksResponse;
import org.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.cloudhub.meta.server.service.node.*;
import org.cloudhub.rpc.GrpcServiceStubPool;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileDeleteService {
    private final NodeAllocator nodeAllocator;
    private final ServerChecker serverChecker;
    private final FileStorageLocationRepository repository;
    private final NodeChannelPool nodeChannelPool;
    private final GrpcServiceStubPool<BlockDeleteServiceGrpc.BlockDeleteServiceStub>
            blockDeleteServiceStubPool;
    private final Logger logger = LoggerFactory.getLogger(FileDeleteService.class);

    public FileDeleteService(HeartbeatService heartbeatService,
                             FileStorageLocationRepository repository,
                             NodeChannelPool nodeChannelPool) {
        this.nodeAllocator = heartbeatService.getNodeAllocator();
        this.serverChecker = heartbeatService.getServerChecker();
        this.repository = repository;
        this.nodeChannelPool = nodeChannelPool;
        this.blockDeleteServiceStubPool = new GrpcServiceStubPool<>();
    }

    public void deleteFileCompletely(String fileId) {
        FileStorageLocation location = repository.getByFileId(fileId);
        if (location == null) {
            logger.debug("File not exist, fileId={}", fileId);
            return;
        }
        String master = location.getMasterServerId();
        String[] replicas = location.getReplicaIds();
        List<SerializedFileServer> replicaServers = buildReplicaServers(replicas);
        if (!serverChecker.isActive(master)) {
            return;
            // If master dead, we cannot to know how to delete it.
        }
        logger.debug("Master active, delete fileId={}", fileId);
        NodeServer server = nodeAllocator.findNodeServer(master);
        BlockDeleteServiceGrpc.BlockDeleteServiceStub stub =
                requireStub(server);
        DeleteBlocksRequest request = DeleteBlocksRequest.newBuilder()
                .setFileId(fileId)
                .addAllServers(replicaServers)
                .build();
        stub.deleteBlocks(request, DeleteResponseStreamObserver.getInstance());
        logger.debug("Master with replicas delete complete.");
        repository.delete(location);
    }

    private void tryToDelete(FileStorageLocation location) {

    }

    private static class DeleteResponseStreamObserver implements StreamObserver<DeleteBlocksResponse> {

        @Override
        public void onNext(DeleteBlocksResponse value) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }

        public static DeleteResponseStreamObserver getInstance() {
            return SingletonHolder.INSTANCE;
        }

        private static final class SingletonHolder {
            static final DeleteResponseStreamObserver INSTANCE = new DeleteResponseStreamObserver();
        }
    }


    private List<SerializedFileServer> buildReplicaServers(String[] replicas) {
        List<NodeServer> nodeServers = new ArrayList<>();
        for (String replica : replicas) {
            if (serverChecker.isActive(replica)) {
                NodeServer server = nodeAllocator.findNodeServer(replica);
                nodeServers.add(server);
            }
        }
        return RequestServer.toSerialized(nodeServers);
    }

    private BlockDeleteServiceGrpc.BlockDeleteServiceStub requireStub(NodeServer server) {
        ManagedChannel channel = nodeChannelPool.getChannel(server);
        BlockDeleteServiceGrpc.BlockDeleteServiceStub stub =
                blockDeleteServiceStubPool.getStub(server.id());
        if (stub != null) {
            return stub;
        }
        stub = BlockDeleteServiceGrpc.newStub(channel);
        blockDeleteServiceStubPool.registerStub(server.id(), stub);
        return stub;
    }

}
