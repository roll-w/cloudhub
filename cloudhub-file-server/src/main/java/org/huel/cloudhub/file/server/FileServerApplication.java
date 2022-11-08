package org.huel.cloudhub.file.server;

import io.grpc.Server;
import org.huel.cloudhub.file.conf.ConfigLoader;
import org.huel.cloudhub.file.server.service.heartbeat.HeartbeatTask;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * File server start class.
 *
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync(proxyTargetClass = true)
public class FileServerApplication implements ApplicationRunner {
    private final Server server;

    public FileServerApplication(Server server,
                                 HeartbeatTask heartbeatTask) {
        this.server = server;
        this.heartbeatTask = heartbeatTask;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication application =
                new SpringApplication(FileServerApplication.class);
        ConfigLoader loader = ConfigLoader.tryOpenDefault();

        Map<String, Object> overrideProperties = new HashMap<>();
        overrideProperties.put("server.port", loader.getWebPort());

        application.setDefaultProperties(overrideProperties);
        application.run();
    }

    private final HeartbeatTask heartbeatTask;

    @PostConstruct
    public void startSendHeartbeat() {
        heartbeatTask.startSendHeartbeat();
    }

    @PostConstruct
    public void startServer() throws IOException {
        server.start();
    }


    @Override
    public void run(ApplicationArguments args) {
    }


}
