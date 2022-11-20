package org.huel.cloudhub.client.configuration;

import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author RollW
 */
@Configuration
public class ClientRuntimeConfiguration {
    private final ClientConfigLoader clientConfigLoader;

    public ClientRuntimeConfiguration() throws IOException {
        this.clientConfigLoader = ClientConfigLoader.tryOpenDefault();
    }

    @Bean
    public GrpcProperties grpcProperties() {
        return new GrpcProperties(
                clientConfigLoader.getRpcPort(),
                clientConfigLoader.getRpcMaxInboundSize());
    }

    @Bean
    public ClientConfigLoader getClientConfigLoader() {
        return clientConfigLoader;
    }

}
