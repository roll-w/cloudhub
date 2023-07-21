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
import org.cloudhub.file.rpc.container.ContainerStatusRequest;
import org.cloudhub.file.rpc.container.ContainerStatusResponse;
import org.cloudhub.file.rpc.container.SerializedContainerInfo;
import org.cloudhub.file.server.service.container.ContainerService;
import org.cloudhub.file.server.service.container.ReplicaContainerDelegate;
import org.cloudhub.file.fs.container.Container;
import org.cloudhub.file.rpc.container.ContainerStatusRequest;
import org.cloudhub.file.rpc.container.ContainerStatusResponse;
import org.cloudhub.file.rpc.container.ContainerStatusServiceGrpc;
import org.cloudhub.file.rpc.container.SerializedContainerInfo;
import org.cloudhub.file.server.service.container.ContainerService;
import org.cloudhub.file.server.service.container.ReplicaContainerDelegate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RollW
 */
@Service
public class ContainerStatusService extends ContainerStatusServiceGrpc.ContainerStatusServiceImplBase {
    private final ContainerService containerService;
    private final ReplicaContainerDelegate replicaContainerDelegate;

    public ContainerStatusService(ContainerService containerService,
                                  ReplicaContainerDelegate replicaContainerDelegate) {
        this.containerService = containerService;
        this.replicaContainerDelegate = replicaContainerDelegate;
    }

    @Override
    public void getContainerInfo(ContainerStatusRequest request,
                                 StreamObserver<ContainerStatusResponse> responseObserver) {
        Collection<Container> locals = containerService.listContainers();
        Collection<Container> replicas = replicaContainerDelegate.listContainers();
        List<SerializedContainerInfo> serializes  = toSerializedStatus(locals);
        serializes.addAll(toSerializedStatus(replicas));
        responseObserver.onNext(ContainerStatusResponse.newBuilder()
                .addAllInfos(serializes)
                .build());
        responseObserver.onCompleted();
    }

    private List<SerializedContainerInfo> toSerializedStatus(Collection<Container> containers) {
        return containers.stream().map(container ->
                        SerializedContainerInfo.newBuilder()
                                .setContainerId(container.getIdentity().id())
                                .setSerial(container.getSerial())
                                .setLocator(container.getLocator())
                                .setSource(container.getSource())
                                .setLimitBlocks(container.getIdentity().blockLimit())
                                .setLimitMbs((container.getIdentity().blockLimit() *
                                        container.getIdentity().blockSize()) / 1024)
                                .setUsedBlocks(container.getUsedBlocksCount())
                                .setBlockSize(container.getIdentity().blockSize())
                                .build())
                .collect(Collectors.toList());
    }
}
