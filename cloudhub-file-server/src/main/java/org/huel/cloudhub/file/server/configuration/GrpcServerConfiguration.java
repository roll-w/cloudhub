package org.huel.cloudhub.file.server.configuration;

import io.grpc.*;
import org.huel.cloudhub.file.server.service.file.BlockReceiveService;
import org.huel.cloudhub.file.server.service.file.BlockDownloadService;
import org.huel.cloudhub.file.server.service.heartbeat.HeartbeatHostProperties;
import org.huel.cloudhub.server.GrpcProperties;
import org.huel.cloudhub.server.file.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Configuration
@EnableConfigurationProperties(GrpcProperties.class)
public class GrpcServerConfiguration {

    private final HeartbeatHostProperties heartbeatHostProperties;
    private final BlockReceiveService blockReceiveService;
    private final BlockDownloadService blockDownloadService;
    private final GrpcProperties grpcProperties;
    private final FileProperties fileProperties;

    public GrpcServerConfiguration(HeartbeatHostProperties heartbeatHostProperties,
                                   BlockReceiveService blockReceiveService,
                                   BlockDownloadService blockDownloadService,
                                   GrpcProperties grpcProperties,
                                   FileProperties fileProperties) {
        this.heartbeatHostProperties = heartbeatHostProperties;
        this.blockReceiveService = blockReceiveService;
        this.blockDownloadService = blockDownloadService;
        this.grpcProperties = grpcProperties;
        this.fileProperties = fileProperties;
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
                .maxInboundMessageSize((int) fileProperties.getMaxRequestSizeBytes())
                .maxConnectionAge(2, TimeUnit.MINUTES)
                .handshakeTimeout(2, TimeUnit.MINUTES)
                .addService(blockReceiveService)
                .addService(blockDownloadService)
                .build();
    }
}
