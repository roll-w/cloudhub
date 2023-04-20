package org.huel.cloudhub.meta.server;

import io.grpc.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MetaServerApplication {
    private final Server server;

    public MetaServerApplication(Server server) {
        this.server = server;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication application =
                new SpringApplication(MetaServerApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run();
    }

    @PostConstruct
    public void runServer() throws IOException {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.shutdown();
    }

}
