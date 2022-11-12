package org.huel.cloudhub.meta.server;

import io.grpc.Server;
import org.huel.cloudhub.meta.conf.MetaConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        MetaConfigLoader loader = MetaConfigLoader.tryOpenDefault();

        Map<String, Object> overrideProperties = new HashMap<>();
        overrideProperties.put("server.port", loader.getWebPort());

        application.setDefaultProperties(overrideProperties);
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
