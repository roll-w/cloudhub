package org.huel.cloudhub.client.disk.configuration;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
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
    public DiskDatabase diskDatabase(DatasourceConfig datasourceConfig){
        return Light.databaseBuilder(DiskDatabase.class, MySQLDialectProvider.class)
                .setConnectionPool(HikariConnectionPool.class)
                .setLogger(LightSlf4jLogger.createLogger(DiskDatabase.class))
                .datasource(datasourceConfig)
                .build();
    }
}
