package org.huel.cloudhub.rpc;

import io.grpc.ManagedChannel;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public abstract class GrpcChannelPool<K> {
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
}
