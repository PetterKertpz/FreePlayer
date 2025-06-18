package com.freeplayer.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public final class DBConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(DBConnectionPool.class);
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DBConfig.getUrl());
        config.setUsername(DBConfig.getUser());
        config.setPassword(DBConfig.getPassword());
        config.setMaximumPoolSize(DBConfig.getPoolSize());

        dataSource = new HikariDataSource(config);
        logger.info("🔗 Pool de conexiones inicializado correctamente.");
    }

    private DBConnectionPool() {}

    public static Optional<Connection> getConnection() {
        try {
            return Optional.ofNullable(dataSource.getConnection());
        } catch (SQLException e) {
            logger.error("❌ Error al obtener conexión del pool.", e);
            return Optional.empty();
        }
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("🔒 Pool de conexiones cerrado.");
        }
    }
}
