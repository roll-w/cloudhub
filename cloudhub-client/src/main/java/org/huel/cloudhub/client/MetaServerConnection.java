package org.huel.cloudhub.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
