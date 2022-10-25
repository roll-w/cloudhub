package org.huel.cloudhub.file.server.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.file.server.file.BlockReceiveService;
import org.huel.cloudhub.file.server.heartbeat.HeartbeatHostProperties;
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

    private final HeartbeatHostProperties heartbeatHostProperties;
    private final BlockReceiveService blockReceiveService;
    private final GrpcProperties grpcProperties;

    public GrpcServerConfiguration(HeartbeatHostProperties heartbeatHostProperties,
                                   BlockReceiveService blockReceiveService,
                                   GrpcProperties grpcProperties) {
        this.heartbeatHostProperties = heartbeatHostProperties;
        this.blockReceiveService = blockReceiveService;
        this.grpcProperties = grpcProperties;
    }

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forTarget(heartbeatHostProperties.getAddress())
                .usePlaintext()
                .build();
    }

    @Bean
    public Server grpcServer() {
        return ServerBuilder.forPort(grpcProperties.getPort())
                .maxInboundMessageSize(1024 * 1024 * 1024)
                .addService(blockReceiveService)
                .build();
    }
}
