package org.huel.cloudhub.meta.server.configuration;

import org.huel.cloudhub.meta.server.data.database.MetaDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.lingu.light.DatasourceConfig;
import space.lingu.light.Light;
import space.lingu.light.connect.HikariConnectionPool;
import space.lingu.light.log.LightSlf4jLogger;
import space.lingu.light.sql.SQLiteDialectProvider;

/**
 * @author RollW
 */
@Configuration
public class MetaDatabaseConfiguration {

    @Bean
    public MetaDatabase metaDatabase(DatasourceConfig datasourceConfig) {
        return Light.databaseBuilder(MetaDatabase.class, SQLiteDialectProvider.class)
                .datasource(datasourceConfig)
                .setLogger(LightSlf4jLogger.createLogger(MetaDatabase.class))
                .setConnectionPool(HikariConnectionPool.class)
                .build();
    }
}
