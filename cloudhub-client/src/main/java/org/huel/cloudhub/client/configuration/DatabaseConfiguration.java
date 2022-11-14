package org.huel.cloudhub.client.configuration;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.HikariConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.lingu.light.Light;
import space.lingu.light.log.LightSlf4jLogger;
import space.lingu.light.sql.MySQLDialectProvider;

/**
 * @author RollW
 */
@Configuration
public class DatabaseConfiguration {

    @Bean
    public CloudhubDatabase cloudhubDatabase() {
        return Light.databaseBuilder(CloudhubDatabase.class,
                        MySQLDialectProvider.class)
                .setConnectionPool(HikariConnectionPool.class)
                .setLogger(LightSlf4jLogger.createLogger(CloudhubDatabase.class))
                .deleteOnConflict()
                .build();
    }
}
