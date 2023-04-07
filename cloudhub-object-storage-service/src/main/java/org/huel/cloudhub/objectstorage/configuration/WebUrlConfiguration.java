package org.huel.cloudhub.objectstorage.configuration;

import org.huel.cloudhub.objectstorage.configuration.properties.WebUrlsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
@EnableConfigurationProperties(WebUrlsProperties.class)
public class WebUrlConfiguration {
}
