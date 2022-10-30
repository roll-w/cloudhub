package org.huel.cloudhub.meta.server.configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.meta.server.service.node.HeartbeatService;
import org.huel.cloudhub.server.GrpcProperties;
import org.huel.cloudhub.server.file.FileProperties;
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
    private final FileProperties fileProperties;

    public GrpcServerConfiguration(GrpcProperties grpcProperties,
                                   HeartbeatService heartbeatService,
                                   FileProperties fileProperties) {
        this.grpcProperties = grpcProperties;
        this.heartbeatService = heartbeatService;
        this.fileProperties = fileProperties;
    }


    @Bean
    public Server grpcServer() {
        return ServerBuilder.forPort(grpcProperties.getPort())
                .maxInboundMessageSize((int) fileProperties.getMaxRequestSizeBytes())
                .addService(heartbeatService)
                .build();
    }
}
