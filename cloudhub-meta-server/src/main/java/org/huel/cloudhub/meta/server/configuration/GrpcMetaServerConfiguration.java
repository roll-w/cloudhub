package org.huel.cloudhub.meta.server.configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.meta.server.service.file.FileStatusService;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.meta.server.service.node.NodeChannelPool;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Configuration
public class GrpcMetaServerConfiguration {
    private final GrpcProperties grpcProperties;
    private final HeartbeatService heartbeatService;
    private final FileStatusService fileStatusService;

    public GrpcMetaServerConfiguration(GrpcProperties grpcProperties,
                                       HeartbeatService heartbeatService,
                                       FileStatusService fileStatusService) {
        this.grpcProperties = grpcProperties;
        this.heartbeatService = heartbeatService;
        this.fileStatusService = fileStatusService;
    }

    @Bean
    public Server grpcServer() {
        return ServerBuilder.forPort(grpcProperties.getPort())
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes())
                .handshakeTimeout(20, TimeUnit.SECONDS)
                .maxConnectionAge(2, TimeUnit.MINUTES)
                .addService(heartbeatService)
                .addService(fileStatusService)
                .build();
    }

    @Bean
    public NodeChannelPool nodeChannelPool() {
        return new NodeChannelPool(grpcProperties);
    }
}
