package org.huel.cloudhub.client.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;
import java.util.Objects;

/**
 * @author RollW
 */
@ConfigurationProperties(value = "web-url")
public final class WebUrlsProperties {
    private final String backendUrl;
    private final String frontendUrl;
    private final List<String> allowedOrigins;

    @ConstructorBinding
    public WebUrlsProperties(String backendUrl,
                             String frontendUrl,
                             List<String> allowedOrigins) {
        this.backendUrl = backendUrl;
        this.frontendUrl = frontendUrl;
        this.allowedOrigins = allowedOrigins;
    }

    public String getBackendUrl() {
        return backendUrl;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WebUrlsProperties) obj;
        return Objects.equals(this.backendUrl, that.backendUrl) &&
                Objects.equals(this.frontendUrl, that.frontendUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backendUrl, frontendUrl);
    }

    @Override
    public String toString() {
        return "WebUrls[" +
                "backendUrl=" + backendUrl + ", " +
                "frontendUrl=" + frontendUrl + ']';
    }

}
