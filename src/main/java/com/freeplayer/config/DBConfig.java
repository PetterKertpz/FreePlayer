package com.freeplayer.config;

public final class DBConfig {

    private DBConfig() {}

    public static String getUrl() {
        return DBPropertiesLoader.get("db.url");
    }

    public static String getUser() {
        return DBPropertiesLoader.get("db.user");
    }

    public static String getPassword() {
        return DBPropertiesLoader.get("db.password");
    }

    public static int getPoolSize() {
        try {
            return Integer.parseInt(DBPropertiesLoader.get("db.pool.size"));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("El valor de 'db.pool.size' debe ser un número entero.", e);
        }
    }

}
