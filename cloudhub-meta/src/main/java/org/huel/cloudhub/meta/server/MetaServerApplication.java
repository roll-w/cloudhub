package org.huel.cloudhub.meta.server;

import io.grpc.Server;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MetaServerApplication implements ApplicationRunner {
    private final Server server;

    public MetaServerApplication(Server server) {
        this.server = server;
    }

    public static void main(String[] args) {
        SpringApplication.run(MetaServerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        server.start();
    }
}
