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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage gRPC channels.
 *
 * @author RollW
 */
public abstract class GrpcChannelPool<K> implements Closeable {
    private final Map<K, ManagedChannel> channelMap = new HashMap<>();

    public GrpcChannelPool() {
    }

    @NonNull
    protected abstract ManagedChannel buildChannel(K key);

    /**
     * Establish a new channel.
     */
    protected ManagedChannel establish(K key) {
        ManagedChannel managedChannel = buildChannel(key);
        channelMap.put(key, managedChannel);
        return managedChannel;
    }

    public ManagedChannel getChannel(K key) {
        if (key == null) {
            return null;
        }

        if (channelMap.containsKey(key)) {
            ManagedChannel channel = channelMap.get(key);
            if (channel.isShutdown()) {
                return establish(key);
            }
            return channel;
        }
        return establish(key);
    }

    public void disconnect(K key) {
        if (!channelMap.containsKey(key)) {
            return;
        }
        channelMap.get(key).shutdown();
    }

    @Override
    public void close() throws IOException {
        for (ManagedChannel value : channelMap.values()) {
            value.shutdown();
        }
    }
}
