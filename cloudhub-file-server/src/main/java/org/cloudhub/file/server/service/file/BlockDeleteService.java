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

package org.cloudhub.file.server.service.file;

import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.cloudhub.file.fs.block.BlockGroupsInfo;
import org.cloudhub.file.fs.block.BlockMetaInfo;
import org.cloudhub.file.fs.container.Container;
import org.cloudhub.file.fs.container.ContainerAllocator;
import org.cloudhub.file.fs.container.ContainerDeleter;
import org.cloudhub.file.fs.container.ContainerFinder;
import org.cloudhub.file.fs.meta.MetadataException;
import org.cloudhub.file.rpc.block.BlockDeleteServiceGrpc;
import org.cloudhub.file.rpc.block.DeleteBlocksRequest;
import org.cloudhub.file.rpc.block.DeleteBlocksResponse;
import org.cloudhub.file.server.service.SourceServerGetter;
import org.cloudhub.file.server.service.replica.ReplicaService;
import org.cloudhub.file.server.service.replica.ReplicaSynchroPart;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class BlockDeleteService extends BlockDeleteServiceGrpc.BlockDeleteServiceImplBase {
    private final ContainerAllocator containerAllocator;
    private final ContainerFinder containerFinder;
    private final ContainerDeleter containerDeleter;
    private final ReplicaService replicaService;
    private final Logger logger = LoggerFactory.getLogger(BlockDeleteService.class);
    private final SourceServerGetter.ServerInfo serverInfo;

    public BlockDeleteService(ContainerAllocator containerAllocator,
                              ContainerFinder containerFinder,
                              ContainerDeleter containerDeleter,
                              ReplicaService replicaService, SourceServerGetter sourceServerGetter) {
        this.containerAllocator = containerAllocator;
        this.containerFinder = containerFinder;
        this.containerDeleter = containerDeleter;
        this.replicaService = replicaService;
        this.serverInfo = sourceServerGetter.getLocalServer();
    }

    @Override
    public void deleteBlocks(DeleteBlocksRequest request,
                             StreamObserver<DeleteBlocksResponse> responseObserver) {
        final String fileId = request.getFileId();
        List<SerializedFileServer> replicaServers = request.getServersList();
        final String source = getSourceId(request);

        List<Container> containers =
                containerFinder.findContainersByFile(fileId, ContainerFinder.LOCAL);
        Context context = Context.current().fork();
        List<ReplicaSynchroPart> parts = new ArrayList<>();
        for (Container container : containers) {
            try {
                boolean containerDelete = releaseBlockOccupation(
                        fileId, source, container, replicaServers);
                if (containerDelete) {
                    continue;
                }
                ReplicaSynchroPart part =
                        new ReplicaSynchroPart(container, BlockGroupsInfo.EMPTY, 0);
                parts.add(part);
            } catch (IOException | MetadataException e) {
                responseObserver.onError(Status.INTERNAL.asException());
                logger.error("Delete blocks error: ", e);
                return;
            }
        }
        responseObserver.onNext(DeleteBlocksResponse.newBuilder().build());
        responseObserver.onCompleted();
        context.run(() -> sendReplicaSynchroRequest(parts, replicaServers));
    }

    private String getSourceId(DeleteBlocksRequest request) {
        if (request.hasSource()) {
            return request.getSource();
        }
        return serverInfo.id();
    }

    private boolean releaseBlockOccupation(String fileId, String source,
                                           Container container,
                                           List<SerializedFileServer> servers) throws IOException, MetadataException {
        List<BlockMetaInfo> newInfos = new ArrayList<>(container.getBlockMetaInfos());
        if (newInfos.isEmpty()) {
            return false;
        }
        BlockMetaInfo blockMetaInfo = container.getBlockMetaInfoByFile(fileId);
        newInfos.remove(blockMetaInfo);
        container.setBlockMetaInfos(newInfos);
        Context context = Context.current().fork();
        if (container.getUsedBlocksCount() == 0) {
            containerDeleter.deleteContainer(container);
            context.run(() ->
                    startReplicaDeleteRequest(container, source, servers));
            return true;
        }
        containerAllocator.updatesContainerMetadata(container);
        return false;
    }

    private void startReplicaDeleteRequest(Container container, String source,
                                           List<SerializedFileServer> servers) {
        servers.forEach(server ->
                replicaService.requestReplicaDelete(container, source, server));
    }

    private void sendReplicaSynchroRequest(List<ReplicaSynchroPart> parts,
                                           List<SerializedFileServer> servers) {
        servers.forEach(server -> replicaService.requestReplicasSynchro(parts, server));
    }
}
