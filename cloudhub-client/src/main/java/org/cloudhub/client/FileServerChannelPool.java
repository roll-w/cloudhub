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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.cloudhub.rpc.GrpcChannelPool;
import org.cloudhub.rpc.GrpcProperties;
import org.cloudhub.server.rpc.server.SerializedFileServer;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class FileServerChannelPool extends GrpcChannelPool<SerializedFileServer> {
    private final GrpcProperties grpcProperties;
    private final ChannelConfigure configure;

    public FileServerChannelPool(GrpcProperties grpcProperties) {
        this(grpcProperties, ($) -> {
        });
    }


    public FileServerChannelPool(GrpcProperties grpcProperties,
                                 ChannelConfigure configure) {
        this.grpcProperties = grpcProperties;
        this.configure = configure;
    }

    @Override
    @NonNull
    protected ManagedChannel buildChannel(SerializedFileServer key) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(key.getHost(), key.getPort())
                .usePlaintext()
                .keepAliveTime(300, TimeUnit.DAYS)
                .keepAliveTimeout(30, TimeUnit.MINUTES);
        configure.configure(builder);
        builder.maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes() * 2);
        return builder.build();
    }
}
