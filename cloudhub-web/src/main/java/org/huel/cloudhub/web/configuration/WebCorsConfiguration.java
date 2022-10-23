package org.huel.cloudhub.web.configuration;

import org.huel.cloudhub.web.configuration.properties.WebUrlsProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author RollW
 */
@Configuration
public class WebCorsConfiguration {
    final WebUrlsProperties webUrlsProperties;

    public WebCorsConfiguration(WebUrlsProperties webUrlsProperties) {
        this.webUrlsProperties = webUrlsProperties;
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(webUrlsProperties.getAllowedOrigins());
        config.addAllowedMethod("*");

        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        config.addAllowedHeader("*");
        config.addExposedHeader("*");
        return config;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource configSource =
                new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", corsConfiguration());
        return configSource;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource configSource =
                new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", corsConfiguration());

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
                new CorsFilter(configSource));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
