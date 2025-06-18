package com.freeplayer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DBPropertiesLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBPropertiesLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("Archivo config.properties no encontrado en /resources");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar config.properties", e);
        }
    }

    private DBPropertiesLoader() {}

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Clave no encontrada en config.properties: " + key);
        }
        return value;
    }

}
