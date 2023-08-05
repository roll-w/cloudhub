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

import io.grpc.stub.StreamObserver;
import org.cloudhub.meta.server.data.database.repository.FileStorageLocationRepository;
import org.cloudhub.meta.server.data.entity.FileStorageLocation;
import org.cloudhub.meta.server.service.node.HeartbeatService;
import org.cloudhub.meta.server.service.node.NodeAllocator;
import org.cloudhub.meta.server.service.node.FileNodeServer;
import org.cloudhub.meta.server.service.node.ServerChecker;
import org.cloudhub.server.rpc.server.SerializedFileServer;
import org.cloudhub.client.rpc.file.FileAllocateRequest;
import org.cloudhub.client.rpc.file.FileAllocateResponse;
import org.cloudhub.client.rpc.file.FileStatusRequest;
import org.cloudhub.client.rpc.file.FileStatusResponse;
import org.cloudhub.client.rpc.file.FileStatusServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileStatusService extends FileStatusServiceGrpc.FileStatusServiceImplBase {
    private final FileStorageLocationRepository fileStorageLocationRepository;
    private final ServerChecker serverChecker;
    private final NodeAllocator nodeAllocator;

    public FileStatusService(FileStorageLocationRepository fileStorageLocationRepository,
                             HeartbeatService heartbeatService) {
        this.fileStorageLocationRepository = fileStorageLocationRepository;
        this.serverChecker = heartbeatService.getServerChecker();
        this.nodeAllocator = heartbeatService.getNodeAllocator();
    }


    @Override
    public void checkFileStatus(FileStatusRequest request, StreamObserver<FileStatusResponse> responseObserver) {
        String fileId = request.getFileId();
        FileStorageLocation location =
                fileStorageLocationRepository.getByFileId(fileId);
        if (location == null) {
            responseObserver.onNext(FileStatusResponse.newBuilder()
                    .setMasterId("")
                    .build());
            responseObserver.onCompleted();
            return;
        }
        List<FileStorageLocation> locations =
                fileStorageLocationRepository.getLocationsByFileId(fileId);
        ActiveServers activeServers = getActiveServers(locations);
        responseObserver.onNext(FileStatusResponse.newBuilder()
                .setMasterId(activeServers.masterId())
                .addAllServers(activeServers.servers())
                .build());
        responseObserver.onCompleted();
        // TODO: get active servers
    }

    @Override
    public void allocateFileServers(FileAllocateRequest request,
                                    StreamObserver<FileAllocateResponse> responseObserver) {
    }

    private record ActiveServers(
            List<SerializedFileServer> servers,
            String masterId
    ) {
    }

    private ActiveServers getActiveServers(List<FileStorageLocation> locations) {
        for (FileStorageLocation location : locations) {
            List<SerializedFileServer> servers = checkServers(location);
            if (!servers.isEmpty()) {
                return new ActiveServers(servers, location.getMasterServerId());
            }
        }
        return new ActiveServers(List.of(), "");
    }

    private List<SerializedFileServer> checkServers(FileStorageLocation location) {
        List<SerializedFileServer> servers = new ArrayList<>();
        for (String s : location.getServerList()) {
            // select all active servers.
            if (!serverChecker.isActive(s)) {
                continue;
            }
            FileNodeServer server = nodeAllocator.findNodeServer(s);
            if (server == null) {
                continue;
            }
            servers.add(RequestServer.toSerialized(server));
        }
        return servers;
    }
}
