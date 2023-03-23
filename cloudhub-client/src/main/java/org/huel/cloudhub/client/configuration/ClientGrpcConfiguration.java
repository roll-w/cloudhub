package org.huel.cloudhub.client.configuration;

import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.huel.cloudhub.fs.CFSClient;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
public class ClientGrpcConfiguration {
    @Bean
    public CFSClient cfsClient(ClientConfigLoader clientConfigLoader, GrpcProperties grpcProperties) {
        return new CFSClient(
                clientConfigLoader.getMetaServerAddress(),
                clientConfigLoader.getRpcPort(),
                grpcProperties
        );
    }
}
