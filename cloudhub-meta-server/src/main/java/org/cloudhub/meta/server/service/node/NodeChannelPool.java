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

package org.cloudhub.meta.server.service.node;

import io.grpc.ManagedChannel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.cloudhub.rpc.GrpcChannelPool;
import org.cloudhub.rpc.GrpcProperties;

import java.util.concurrent.TimeUnit;

/**
 * Manage gRPC channels through {@link FileNodeServer}.
 *
 * @author RollW
 */
public class NodeChannelPool extends GrpcChannelPool<FileNodeServer>
        implements ServerEventRegistry.ServerEventCallback {
    private final GrpcProperties grpcProperties;

    public NodeChannelPool(GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
    }

    @Override
    @NonNull
    protected ManagedChannel buildChannel(NodeServer server) {
        return ManagedChannelBuilder.forAddress(server.host(), server.port())
                .usePlaintext()
                .keepAliveTime(300, TimeUnit.DAYS)
                .keepAliveTimeout(30, TimeUnit.MINUTES)
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes() * 2)
                .build();
    }

    @Override
    public void registerServer(FileNodeServer server) {
    }

    @Override
    public void removeActiveServer(FileNodeServer nodeServer) {
        disconnect(nodeServer);
    }

    @Override
    public void addActiveServer(FileNodeServer nodeServer) {
        establish(nodeServer);
    }
}
