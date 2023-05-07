package org.huel.cloudhub.client.disk;

import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@SpringBootApplication
public class DiskClientApplication {
    private static ConfigurableApplicationContext sContext;

    public static void main(String[] args) throws IOException {
        SpringApplication application =
                new SpringApplication(DiskClientApplication.class);
        ClientConfigLoader loader =
                ClientConfigLoader.tryOpenDefault(DiskClientApplication.class);
        int port = loader.getWebPort(7015);

        Map<String, Object> overrideProperties = new HashMap<>();
        overrideProperties.put("server.port", port);
        overrideProperties.put("spring.mvc.throw-exception-if-no-handler-found", true);

        application.setDefaultProperties(overrideProperties);
        sContext = application.run();
    }
}
