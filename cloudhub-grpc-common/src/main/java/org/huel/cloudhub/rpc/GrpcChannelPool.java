package org.huel.cloudhub.rpc;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public abstract class GrpcChannelPool<K> implements Closeable {
    private final Map<K, ManagedChannel> channelMap = new HashMap<>();

    public GrpcChannelPool() {
    }

    @NonNull
    protected abstract ManagedChannel buildChannel(K key);

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
            ConnectivityState state = channel.getState(true);
            if (state == ConnectivityState.TRANSIENT_FAILURE) {
                channel.shutdown();
                return establish(key);
            }
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
