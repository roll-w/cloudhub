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
import org.cloudhub.rpc.ChannelConfigure;
import space.lingu.NonNull;

import java.io.Closeable;

/**
 * @author RollW
 */
public class MetaServerConnection implements Closeable {
    private final String host;
    private final int port;

    private final ManagedChannel managedChannel;

    public MetaServerConnection(String host, int port) {
        this(host, port, ($) -> {});
    }

    public MetaServerConnection(String target) {
        this(target, ($) -> {});
    }

    public MetaServerConnection(String host, int port,
                                @NonNull ChannelConfigure configure) {
        this.managedChannel = buildChannel(host, port, configure);
        this.host = host;
        this.port = port;
    }


    public MetaServerConnection(String target,
                                @NonNull ChannelConfigure configure) {
        this.managedChannel = buildChannel(target, configure);
        this.host = null;
        this.port = -1;
    }

    private ManagedChannel buildChannel(String host, int port,
                                        ChannelConfigure configurator) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext();
        configurator.configure(builder);
        return builder.build();
    }

    private ManagedChannel buildChannel(String target,
                                        ChannelConfigure configurator) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(target)
                .usePlaintext();
        configurator.configure(builder);
        return builder.build();
    }

    public ManagedChannel getManagedChannel() {
        return managedChannel;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void close() {
        managedChannel.shutdown();
    }
}
