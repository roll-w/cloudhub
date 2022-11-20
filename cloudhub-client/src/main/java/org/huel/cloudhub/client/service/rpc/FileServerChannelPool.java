package org.huel.cloudhub.client.service.rpc;

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

    public FileServerChannelPool(GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
    }

    @Override
    @NonNull
    protected ManagedChannel buildChannel(SerializedFileServer key) {
        return ManagedChannelBuilder.forAddress(key.getHost(), key.getPort())
                .usePlaintext()
                .keepAliveTime(5, TimeUnit.MINUTES)
                .keepAliveTimeout(2, TimeUnit.MINUTES)
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes() * 2)
                .build();
    }
}
