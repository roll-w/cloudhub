package org.huel.cloudhub.file.server.configuration;

import org.huel.cloudhub.file.fs.LocalFileServer;
import org.huel.cloudhub.server.file.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class FileConfiguration {

    @Bean
    public LocalFileServer localFileServer() {
        return new LocalFileServer();
    }
}
