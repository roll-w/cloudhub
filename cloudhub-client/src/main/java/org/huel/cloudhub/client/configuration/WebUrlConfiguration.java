package org.huel.cloudhub.client.configuration;

import org.huel.cloudhub.client.configuration.properties.WebUrlsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
@EnableConfigurationProperties(WebUrlsProperties.class)
public class WebUrlConfiguration {
}
