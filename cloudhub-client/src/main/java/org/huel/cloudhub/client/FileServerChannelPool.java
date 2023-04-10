package org.huel.cloudhub.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.rpc.GrpcChannelPool;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.huel.cloudhub.server.rpc.server.SerializedFileServer;

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
