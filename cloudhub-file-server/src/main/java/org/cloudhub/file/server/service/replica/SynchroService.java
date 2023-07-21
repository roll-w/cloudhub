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

package org.cloudhub.file.server.service.replica;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.cloudhub.file.fs.block.BlockGroupsInfo;
import org.cloudhub.file.fs.container.ContainerFinder;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.cloudhub.file.fs.block.BlockGroupsInfo;
import org.cloudhub.file.fs.container.Container;
import org.cloudhub.file.fs.container.ContainerFinder;
import org.cloudhub.file.fs.container.ContainerGroup;
import org.cloudhub.file.rpc.replica.SerializedContainerCheckStatus;
import org.cloudhub.file.rpc.synchro.DeleteContainerRequest;
import org.cloudhub.file.rpc.synchro.DeleteContainerResponse;
import org.cloudhub.file.rpc.synchro.SynchroRequest;
import org.cloudhub.file.rpc.synchro.SynchroResponse;
import org.cloudhub.file.rpc.synchro.SynchroServiceGrpc;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author RollW
 */
@Service
public class SynchroService extends SynchroServiceGrpc.SynchroServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(SynchroService.class);

    private final ContainerFinder containerFinder;
    private final CheckService checkService;
    private final ReplicaService replicaService;

    public SynchroService(ContainerFinder containerFinder,
                          CheckService checkService,
                          ReplicaService replicaService) {
        this.containerFinder = containerFinder;
        this.checkService = checkService;
        this.replicaService = replicaService;
    }

    @Override
    public void sendSynchro(SynchroRequest request, StreamObserver<SynchroResponse> responseObserver) {
        List<SerializedFileServer> servers = request.getServersList();
        String source = getSource(request);
        // pre-check all file exists
        for (String id : request.getFileIdsList()) {
            ContainerGroup containerGroup =
                    containerFinder.findContainerGroupByFile(id, source);
            if (containerGroup == null) {
                logger.error("File not found: {}", id);
                responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
                return;
            }
        }

        for (String fileId : request.getFileIdsList()) {
            ContainerGroup group = containerFinder.findContainerGroupByFile(fileId, source);
            servers.forEach(server ->
                    checkServerAndBuildReplica(group, fileId, server));
        }
        SynchroResponse synchroResponse = SynchroResponse.newBuilder()
                .build();
        responseObserver.onNext(synchroResponse);
        responseObserver.onCompleted();
    }

    private void checkServerAndBuildReplica(ContainerGroup group, String fileId,
                                            SerializedFileServer dest) {
        List<Container> containers = group.containersWithFile(fileId);
        List<Long> serials = containers
                .stream().map(Container::getSerial)
                .toList();

        List<SerializedContainerCheckStatus> statuses = checkService.sendContainerCheckRequest(
                group.getContainerId(),
                group.getSourceId(),
                serials,
                dest
        );

        Set<Long> needSync = new HashSet<>();
        List<Long> recvSerials = statuses.stream()
                .map(SerializedContainerCheckStatus::getSerial)
                .toList();
        for (SerializedContainerCheckStatus status : statuses) {
            long serial = status.getSerial();
            String recvCheck = status.getCheckValue();
            Container container = group.getContainer(serial);
            if (!Objects.equals(container.getIdentity().crc(), recvCheck)) {
                needSync.add(serial);
            }
        }
        serials.stream().filter(i -> !recvSerials.contains(i))
                .forEach(needSync::add);
        List<ReplicaSynchroPart> parts = needSync.stream()
                .map(group::getContainer)
                .filter(Objects::nonNull)
                .map(container -> new ReplicaSynchroPart(
                        container,
                        BlockGroupsInfo.build(0, container.getIdentity().blockLimit() - 1),
                        -1)
                )
                .toList();
        sendFullSyncRequest(parts, dest);
    }

    private void sendFullSyncRequest(List<ReplicaSynchroPart> parts,
                                     SerializedFileServer server) {
        replicaService.requestReplicasSynchro(parts, server);
    }

    private String getSource(SynchroRequest request) {
        if (request.hasSource()) {
            return request.getSource();
        }
        // if there's no source, then the current server must be the master.
        return ContainerFinder.LOCAL;
    }


    @Override
    public void deleteContainers(DeleteContainerRequest request,
                                 StreamObserver<DeleteContainerResponse> responseObserver) {
    }

}
