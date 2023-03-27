package org.huel.cloudhub.client.disk.configuration;

import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.huel.cloudhub.client.disk.DiskClientApplication;
import org.huel.cloudhub.rpc.GrpcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.lingu.light.DatasourceConfig;

import java.io.IOException;

/**
 * @author RollW
 */
@Configuration
public class DiskRuntimeConfiguration {
    private final ClientConfigLoader clientConfigLoader;

    public DiskRuntimeConfiguration() throws IOException {
        this.clientConfigLoader = ClientConfigLoader.tryOpenDefault(DiskClientApplication.class);
    }

    @Bean
    public GrpcProperties grpcProperties() {
        return new GrpcProperties(
                clientConfigLoader.getRpcPort(),
                clientConfigLoader.getRpcMaxInboundSize());
    }

    @Bean
    public DatasourceConfig datasourceConfig() {
        return new DatasourceConfig(
                clientConfigLoader.getDatabaseUrl(),
                "com.mysql.cj.jdbc.Driver",
                clientConfigLoader.getDatabaseUsername(),
                clientConfigLoader.getDatabasePassword());
    }


    @Bean
    public ClientConfigLoader getClientConfigLoader() {
        return clientConfigLoader;
    }
}
