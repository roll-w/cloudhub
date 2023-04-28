package org.huel.cloudhub.file.server;

import io.grpc.Server;
import org.huel.cloudhub.file.server.service.heartbeat.HeartbeatTask;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * File server start class.
 *
 * @author RollW
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FileServerApplication implements ApplicationRunner {
    private final Server server;

    public FileServerApplication(Server server,
                                 HeartbeatTask heartbeatTask) {
        this.server = server;
        this.heartbeatTask = heartbeatTask;
    }

    private static ConfigurableApplicationContext sContext;

    public static void main(String[] args) throws Exception {
        SpringApplication application =
                new SpringApplication(FileServerApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        sContext = application.run();
    }

    private final HeartbeatTask heartbeatTask;

    public static void exit(int exitCode) {
        SpringApplication.exit(sContext, () -> exitCode);
    }

    @PostConstruct
    public void startSendHeartbeat() {
        heartbeatTask.startSendHeartbeat();
    }

    @PreDestroy
    public void stopSendHeartbeat() {
        heartbeatTask.stop();
    }

    @PostConstruct
    public void startServer() throws IOException {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.shutdown();
    }

    @Override
    public void run(ApplicationArguments args) {
    }

}
