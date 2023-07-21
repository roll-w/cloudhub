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

import io.grpc.ManagedChannel;
import org.cloudhub.file.fs.container.ContainerFinder;
import org.cloudhub.file.rpc.replica.CheckRequest;
import org.cloudhub.file.rpc.replica.CheckResponse;
import org.cloudhub.file.rpc.replica.CheckServiceGrpc;
import org.cloudhub.file.rpc.replica.SerializedContainerCheckStatus;
import org.cloudhub.file.server.service.ClientFileServerChannelPool;
import org.cloudhub.rpc.GrpcServiceStubPool;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class CheckService {
    private final ClientFileServerChannelPool channelPool;
    private final GrpcServiceStubPool<CheckServiceGrpc.CheckServiceBlockingStub>
            checkServiceStubPool;

    public CheckService(ClientFileServerChannelPool channelPool) {
        this.channelPool = channelPool;
        this.checkServiceStubPool = new GrpcServiceStubPool<>();
    }

    public List<SerializedContainerCheckStatus> sendContainerCheckRequest(String id, String source,
                                                                     List<Long> serials,
                                                                     SerializedFileServer dest) {
        CheckServiceGrpc.CheckServiceBlockingStub stub =
                requireCheckServiceStub(dest);
        CheckRequest.Builder requestBuilder = CheckRequest.newBuilder()
                .setContainerId(id)
                .addAllSerials(serials);
        if (!ContainerFinder.isLocal(source)) {
            requestBuilder.setSource(source);
        }
        CheckResponse response = stub.checkContainers(requestBuilder.build());
        return response.getStatusList();
    }

    private CheckServiceGrpc.CheckServiceBlockingStub requireCheckServiceStub(SerializedFileServer server) {
        ManagedChannel managedChannel = channelPool.getChannel(server);
        CheckServiceGrpc.CheckServiceBlockingStub stub =
                checkServiceStubPool.getStub(server.getId());
        if (stub != null) {
            return stub;
        }
        stub = CheckServiceGrpc.newBlockingStub(managedChannel);
        checkServiceStubPool.registerStub(server.getId(), stub);
        return stub;
    }
}
