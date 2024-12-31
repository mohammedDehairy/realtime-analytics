package com.eldoheiri.databaseaccess;

import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DataSource {
    private static final HikariDataSource DATA_SOURCE = createDataSource();

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String dbUsername = System.getenv("DB_USER_NAME");
        String dbPassword = System.getenv("DB_PASSWORD");
        config.setJdbcUrl("jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.addDataSourceProperty("ssl", "false");
        config.setAutoCommit(false);
        config.setMaximumPoolSize(40);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(300000);
        config.setKeepaliveTime(150000);
        HikariDataSource ds = new HikariDataSource(config);

        return ds;
    }

    public static java.sql.Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
