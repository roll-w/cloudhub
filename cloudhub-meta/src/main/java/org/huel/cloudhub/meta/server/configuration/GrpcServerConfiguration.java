package org.huel.cloudhub.meta.server.configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.meta.server.node.HeartbeatService;
import org.huel.cloudhub.server.GrpcProperties;
import org.springframework.beans.factory.annotation.Autowired;
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

    private HeartbeatService heartbeatService;

    public GrpcServerConfiguration(GrpcProperties grpcProperties) {
        this.grpcProperties = grpcProperties;
    }

    @Autowired
    public void setHeartbeatService(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @Bean
    public Server grpcServer() {
        return ServerBuilder.forPort(grpcProperties.getPort())
                .addService(heartbeatService)
                .build();
    }
}
