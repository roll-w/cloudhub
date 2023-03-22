package org.huel.cloudhub.client;

import org.huel.cloudhub.client.conf.ClientConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@SpringBootApplication
@EnableScheduling
public class ObjectClientApplication {
    private static ConfigurableApplicationContext sContext;

    public static void main(String[] args) throws Exception {
        SpringApplication application =
                new SpringApplication(ObjectClientApplication.class);
        ClientConfigLoader loader =
                ClientConfigLoader.tryOpenDefault(ObjectClientApplication.class);

        Map<String, Object> overrideProperties = new HashMap<>();
        overrideProperties.put("server.port", loader.getWebPort());

        application.setDefaultProperties(overrideProperties);
        sContext = application.run();
    }

    public static void exit(int exitCode) {
        SpringApplication.exit(sContext, () -> exitCode);
    }
}
