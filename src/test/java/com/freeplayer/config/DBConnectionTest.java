package com.freeplayer.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para verificar la funcionalidad del pool de conexiones.
 */
class DBConnectionTest {

    @Test
    @DisplayName("Debería obtener una conexión válida del pool")
    void testShouldGetValidConnectionFromPool() {
        // 1. Llama al metodo para obtener la conexión
        Optional<Connection> connectionOptional = DBConnectionPool.getConnection();

        // 2. Verifica que el Optional contiene un valor (no está vacío)
        assertTrue(connectionOptional.isPresent(), "El pool debería devolver una conexión.");

        // 3. Usa try-with-resources para asegurar que la conexión se devuelva al pool
        //    incluso si las aserciones fallan.
        try (Connection connection = connectionOptional.get()) {

            // 4. Verifica que el objeto de conexión no es nulo
            assertNotNull(connection, "El objeto de conexión no puede ser nulo.");

            // 5. La prueba más importante: verifica que la conexión es válida
            //    El argumento es un timeout en segundos.
            assertTrue(connection.isValid(1), "La conexión obtenida no es válida.");

            System.out.println("✅ ¡Prueba de conexión exitosa! La conexión es válida.");

        } catch (SQLException e) {
            // Si ocurre una excepción SQL, la prueba debe fallar.
            fail("Ocurrió una SQLException durante la prueba de conexión.", e);
        }
    }

    @AfterAll
    @DisplayName("Cerrar el pool de conexiones después de todas las pruebas")
    static void tearDown() {
        // Cierra el DataSource de Hikari para liberar todos los recursos.
        // Es una buena práctica para asegurar una finalización limpia.
        DBConnectionPool.shutdown();
        System.out.println("↪️ Pool de conexiones cerrado correctamente después de las pruebas.");
    }
}