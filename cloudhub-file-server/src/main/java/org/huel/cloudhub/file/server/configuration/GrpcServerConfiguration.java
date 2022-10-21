package org.huel.cloudhub.file.server.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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

    private final GrpcProperties grpcProperties;

    public GrpcServerConfiguration(HeartbeatHostProperties heartbeatHostProperties,
                                   GrpcProperties grpcProperties) {
        this.heartbeatHostProperties = heartbeatHostProperties;
        this.grpcProperties = grpcProperties;
    }

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forTarget(heartbeatHostProperties.getAddress())
                .usePlaintext()
                .build();
    }
}
