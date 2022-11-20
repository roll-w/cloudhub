package org.huel.cloudhub.client.configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.huel.cloudhub.client.service.rpc.FileServerChannelPool;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
public class ClientGrpcConfiguration {
    @Bean
    public FileServerChannelPool fileServerChannelPool(GrpcProperties grpcProperties) {
        return new FileServerChannelPool(grpcProperties);
    }

    @Bean
    public ManagedChannel metaManagedChannel(ClientConfigLoader clientConfigLoader) {
        return ManagedChannelBuilder.forTarget(clientConfigLoader.getMetaServerAddress())
                .usePlaintext()
                .build();
    }
}
