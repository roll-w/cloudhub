package org.huel.cloudhub.meta.server.service.node;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.huel.cloudhub.rpc.GrpcChannelPool;
import org.huel.cloudhub.rpc.GrpcProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class NodeChannelPool extends GrpcChannelPool<NodeServer>
        implements ServerEventRegistry.ServerEventCallback {
    private final GrpcProperties grpcProperties;

    public NodeChannelPool(GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
    }

    @Override
    @NonNull
    protected ManagedChannel buildChannel(NodeServer server) {
        return ManagedChannelBuilder.forAddress(server.host(), server.port())
                .usePlaintext()
                .keepAliveTime(5, TimeUnit.MINUTES)
                .keepAliveTimeout(2, TimeUnit.MINUTES)
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes() * 2)
                .build();
    }

    @Override
    public void registerServer(NodeServer server) {
    }

    @Override
    public void removeActiveServer(NodeServer nodeServer) {
        disconnect(nodeServer);
    }

    @Override
    public void addActiveServer(NodeServer nodeServer) {
        establish(nodeServer);
    }
}
