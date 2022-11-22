package org.huel.cloudhub.client.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import space.lingu.light.DatasourceConfig;
import space.lingu.light.LightLogger;
import space.lingu.light.LightRuntimeException;
import space.lingu.light.connect.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Hikari Connection Pool
 *
 * @author RollW
 */
public class HikariConnectionPool implements ConnectionPool {
    private HikariDataSource source;

    public HikariConnectionPool() {
    }

    @Override
    public void setDataSourceConfig(DatasourceConfig config) {
        if (source != null) {
            return;
        }
        HikariConfig hikariConfig = new HikariConfig();
        setupHikariConfig(hikariConfig);

        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.addDataSourceProperty("maximumPoolSize", "100");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        source = new HikariDataSource(hikariConfig);
    }

    @Override
    public Connection requireConnection() {
        checkPool();
        try {
            return source.getConnection();
        } catch (SQLException e) {
            throw new LightRuntimeException(e);
        }
    }

    @Override
    public void release(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new LightRuntimeException(e);
        }
    }

    private LightLogger logger;

    @Override
    public void setLogger(LightLogger logger) {
        this.logger = logger;
    }

    @Override
    public LightLogger getLogger() {
        return logger;
    }

    @Override
    public void close() {
        checkPool();
        if (source != null) {
            source.close();
        }
    }

    private void checkPool() {
        if (source == null) {
            throw new LightRuntimeException("Not initialize Hikari datasource");
        }
    }

    protected void setupHikariConfig(HikariConfig config) {

    }
}
