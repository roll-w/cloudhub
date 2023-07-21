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
import org.cloudhub.client.rpc.file.ClientFileDeleteRequest;
import org.cloudhub.client.rpc.file.ClientFileDeleteResponse;
import org.cloudhub.client.rpc.file.ClientFileDeleteServiceGrpc;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class ClientFileDeleteDispatchService extends ClientFileDeleteServiceGrpc.ClientFileDeleteServiceImplBase {
    private final FileDeleteService fileDeleteService;

    public ClientFileDeleteDispatchService(FileDeleteService fileDeleteService) {
        this.fileDeleteService = fileDeleteService;
    }

    @Override
    public void deleteFile(ClientFileDeleteRequest request,
                           StreamObserver<ClientFileDeleteResponse> responseObserver) {
        final String fileId = request.getFileId();
        if (fileId.isEmpty()) {
            responseObserver.onError(
                    new IllegalArgumentException("File id is null or empty")
            );
            return;
        }
        fileDeleteService.deleteFileCompletely(fileId);
        responseObserver.onCompleted();
    }
}
