package org.huel.cloudhub.meta.server.configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.server.GrpcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcServerConfiguration {
    private final GrpcProperties grpcProperties;

    private final HeartbeatService heartbeatService;

    public GrpcServerConfiguration(GrpcProperties grpcProperties,
                                   HeartbeatService heartbeatService) {
        this.grpcProperties = grpcProperties;
        this.heartbeatService = heartbeatService;
    }


    @Bean
    public Server grpcServer() {
        return ServerBuilder.forPort(grpcProperties.getPort())
                .addService(heartbeatService)
                .build();
    }
}
