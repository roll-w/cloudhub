package org.huel.cloudhub.meta.server.service.node;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.huel.cloudhub.server.GrpcProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
public class NodeChannelPool {
    private final Map<NodeServer, ManagedChannel> channelMap =
            new HashMap<>();

    private final GrpcProperties grpcProperties;

    public NodeChannelPool(GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
    }

    private ManagedChannel establish(NodeServer server) {
        ManagedChannel managedChannel =
                ManagedChannelBuilder.forAddress(server.host(), server.port())
                        .usePlaintext()
                        .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes() * 2)
                        .build();
        channelMap.put(server, managedChannel);
        return managedChannel;
    }

    public ManagedChannel getChannel(NodeServer server) {
        if (channelMap.containsKey(server)) {
            return channelMap.get(server);
        }
        return establish(server);
    }

    public void disconnect(NodeServer server) {
        if (!channelMap.containsKey(server)) {
            return;
        }
        channelMap.get(server).shutdown();
    }
}
