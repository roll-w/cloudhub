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

package org.cloudhub.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class TargetGrpcChannelPool extends GrpcChannelPool<String> {
    private final GrpcProperties grpcProperties;
    private final ChannelConfigure channelConfigure;

    public TargetGrpcChannelPool(GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
        this.channelConfigure = null;
    }

    public TargetGrpcChannelPool(GrpcProperties grpcProperties,
                                 ChannelConfigure channelConfigure) {
        this.grpcProperties = grpcProperties;
        this.channelConfigure = channelConfigure;
    }

    @Override
    @NonNull
    protected ManagedChannel buildChannel(String target) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .keepAliveTime(300, TimeUnit.DAYS)
                .keepAliveTimeout(30, TimeUnit.MINUTES)
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes() * 2);
        if (channelConfigure != null) {
            channelConfigure.configure(builder);
        }
        return builder.build();
    }

    public ManagedChannel forTarget(String target) {
        return getChannel(target);
    }
}
