package org.huel.cloudhub.meta.server.configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.server.GrpcProperties;
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

    public GrpcMetaServerConfiguration(GrpcProperties grpcProperties,
                                       HeartbeatService heartbeatService) {
        this.grpcProperties = grpcProperties;
        this.heartbeatService = heartbeatService;
    }

    @Bean
    public Server grpcServer() {
        return ServerBuilder.forPort(grpcProperties.getPort())
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes())
                .handshakeTimeout(20, TimeUnit.SECONDS)
                .maxConnectionAge(2, TimeUnit.MINUTES)
                .addService(heartbeatService)
                .build();
    }
}
