/*
 * Cloudhub - A high available, scalable distributed file system.
 * Copyright (C) 2022 Cloudhub
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.cloudhub.meta.server.configuration;

import org.cloudhub.meta.server.data.database.MetaDatabase;
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
