package org.huel.cloudhub.client.data.database;

import com.zaxxer.hikari.HikariConfig;
import space.lingu.light.connect.HikariConnectionPool;

/**
 * Hikari Connection Pool
 *
 * @author RollW
 */
public class CloudhubHikariConnectionPool extends HikariConnectionPool {
    @Override
    protected void setupHikariConfig(HikariConfig config) {
    }
}
