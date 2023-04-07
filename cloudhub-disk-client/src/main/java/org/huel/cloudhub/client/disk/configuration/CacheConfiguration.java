package org.huel.cloudhub.client.disk.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    @Primary
    public CacheManager cacheManager() {
        // TODO: allow set separately expire time for different caches
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine
                .newBuilder()
                .expireAfterAccess(60, TimeUnit.MINUTES)
                .expireAfterWrite(60, TimeUnit.MINUTES);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}