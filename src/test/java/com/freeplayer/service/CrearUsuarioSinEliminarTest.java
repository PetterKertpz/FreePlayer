package com.freeplayer.service;

import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración que crea un usuario y lo deja en la base de datos
 * para permitir la inspección manual de los datos.
 * No limpia los datos después de la ejecución.
 */
class CrearUsuarioSinEliminarTest {

    private final UsuarioService usuarioService = new UsuarioService();
    private final ConfiguracionUsuarioService configService = new ConfiguracionUsuarioService();

    @Test
    @DisplayName("Debería crear un nuevo usuario y dejarlo persistido en la BD")
    void deberiaCrearUsuarioYNoEliminarlo() {
        // --- ARRANGE (Preparar) ---
        String emailUnico = "usuario.persistido" + System.currentTimeMillis() + "@test.com";
        String nombreUsuarioUnico = "tester.persistido" + System.currentTimeMillis();
        String contrasena = "clavePersistente456";
        String pais = "MX"; // México

        System.out.println("--- INICIANDO PRUEBA DE CREACIÓN PERSISTENTE ---");
        System.out.println("Intentando registrar al usuario: " + nombreUsuarioUnico);

        // --- ACT (Actuar) ---
        Usuario usuarioCreado = usuarioService.registrarNuevoUsuario(nombreUsuarioUnico, emailUnico, contrasena, pais);

        // --- ASSERT (Verificar) ---

        // 1. Verificar que el usuario fue creado.
        assertNotNull(usuarioCreado, "El objeto Usuario no debería ser nulo tras el registro.");
        assertTrue(usuarioCreado.getId() > 0, "El usuario debería tener un ID > 0 asignado por la BD.");
        System.out.println("✅ Usuario registrado exitosamente con ID: " + usuarioCreado.getId());

        // 2. Verificar que se le ha asignado una configuración por defecto.
        Optional<ConfiguracionUsuario> configOpt = configService.buscarConfiguracionPorUsuario(usuarioCreado.getId());
        assertTrue(configOpt.isPresent(), "El usuario debería tener una configuración por defecto creada.");
        assertEquals(1, configOpt.get().getTema().getId(), "El tema asignado debería ser el tema por defecto (ID 1).");
        System.out.println("✅ Configuración por defecto verificada.");

        System.out.println("--- PRUEBA COMPLETADA ---");
        System.out.println("El usuario '" + nombreUsuarioUnico + " ha quedado en la base de datos para su inspección.");
    }
}