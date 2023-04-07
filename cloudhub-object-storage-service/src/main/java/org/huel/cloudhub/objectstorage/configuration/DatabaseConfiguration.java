package org.huel.cloudhub.objectstorage.configuration;

import org.huel.cloudhub.objectstorage.data.database.CloudhubDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.lingu.light.DatasourceConfig;
import space.lingu.light.Light;
import space.lingu.light.connect.HikariConnectionPool;
import space.lingu.light.log.LightSlf4jLogger;
import space.lingu.light.sql.MySQLDialectProvider;

/**
 * @author RollW
 */
@Configuration
public class DatabaseConfiguration {

    @Bean
    public CloudhubDatabase cloudhubDatabase(DatasourceConfig datasourceConfig) {
        return Light.databaseBuilder(CloudhubDatabase.class,
                        MySQLDialectProvider.class)
                .setConnectionPool(HikariConnectionPool.class)
                .datasource(datasourceConfig)
                .setLogger(LightSlf4jLogger.createLogger(CloudhubDatabase.class))
                .deleteOnConflict()
                .build();
    }
}
