package org.huel.cloudhub.file.server.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.huel.cloudhub.file.server.service.file.BlockDeleteService;
import org.huel.cloudhub.file.server.service.file.BlockDownloadService;
import org.huel.cloudhub.file.server.service.file.BlockReceiveService;
import org.huel.cloudhub.file.server.service.heartbeat.HeartbeatHostProperties;
import org.huel.cloudhub.file.server.service.replica.ReplicaReceiveService;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Configuration
public class GrpcFileServerConfiguration {
    private final HeartbeatHostProperties heartbeatHostProperties;
    private final BlockReceiveService blockReceiveService;
    private final BlockDownloadService blockDownloadService;
    private final BlockDeleteService blockDeleteService;
    private final ReplicaReceiveService replicaReceiveService;
    private final GrpcProperties grpcProperties;

    public GrpcFileServerConfiguration(HeartbeatHostProperties heartbeatHostProperties,
                                       BlockReceiveService blockReceiveService,
                                       BlockDownloadService blockDownloadService,
                                       BlockDeleteService blockDeleteService,
                                       ReplicaReceiveService replicaReceiveService,
                                       GrpcProperties grpcProperties) {
        this.heartbeatHostProperties = heartbeatHostProperties;
        this.blockReceiveService = blockReceiveService;
        this.blockDownloadService = blockDownloadService;
        this.blockDeleteService = blockDeleteService;
        this.replicaReceiveService = replicaReceiveService;
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
                .maxInboundMessageSize((int) grpcProperties.getMaxRequestSizeBytes())
                .maxConnectionAge(5, TimeUnit.MINUTES)
                .handshakeTimeout(2, TimeUnit.MINUTES)
                .keepAliveTime(5, TimeUnit.MINUTES)
                .addService(blockReceiveService)
                .addService(blockDownloadService)
                .addService(replicaReceiveService)
                .addService(blockDeleteService)
                .build();
    }


}
