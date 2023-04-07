package org.huel.cloudhub.client.disk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author RollW
 */
@EnableAsync(proxyTargetClass = true)
@Configuration
public class AsyncConfiguration {

    @Bean("CDS-Main-Executor")
    public Executor configureExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);
        executor.setMaxPoolSize(40);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(20);
        executor.setThreadNamePrefix("CDS-Main-Executor");

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }
}
