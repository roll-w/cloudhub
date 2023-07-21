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

import org.cloudhub.client.rpc.file.ClientFileDeleteRequest;
import org.cloudhub.client.rpc.file.ClientFileDeleteResponse;
import org.cloudhub.client.rpc.file.ClientFileDeleteServiceGrpc;

/**
 * @author RollW
 */
public class ClientFileDeleteClient {
    private final ClientFileDeleteServiceGrpc.ClientFileDeleteServiceBlockingStub stub;

    public ClientFileDeleteClient(MetaServerConnection connection) {
        this.stub = ClientFileDeleteServiceGrpc.newBlockingStub(connection.getManagedChannel());
    }

    public void permanentlyDeleteFile(String fileId) {
        ClientFileDeleteResponse response = stub.deleteFile(
                ClientFileDeleteRequest.newBuilder()
                        .setFileId(fileId)
                        .build()
        );
    }
}
