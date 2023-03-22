package org.huel.cloudhub.fs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class MetaServerConnection {
    private final String host;
    private final int port;

    private final ManagedChannel managedChannel;

    public MetaServerConnection(String host, int port) {
        this(host, port, ($) -> {});
    }

    public MetaServerConnection(String host, int port,
                                @NonNull ChannelConfigure configure) {
        this.managedChannel = buildChannel(host, port, configure);
        this.host = host;
        this.port = port;
    }

    private ManagedChannel buildChannel(String host, int port,
                                        ChannelConfigure configurator) {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(host, port)
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
}
