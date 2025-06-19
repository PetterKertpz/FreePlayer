package com.freeplayer.service;

import com.freeplayer.dao.ConfiguracionUsuarioDAO;
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.dao.UsuarioDAO;
import com.freeplayer.dao.impl.ConfiguracionUsuarioDAOImpl;
import com.freeplayer.dao.impl.TemaUIDAOImpl;
import com.freeplayer.dao.impl.UsuarioDAOImpl;
import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.Usuario;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración que crea un usuario y su configuración.
 * MEJORA: Ahora limpia los datos después de cada ejecución para asegurar
 * que las pruebas sean independientes y no dejen basura en la BD.
 */
class CrearUsuarioSinEliminarTest {

    private UsuarioService usuarioService;
    private ConfiguracionUsuarioService configService;
    private UsuarioDAO usuarioDAO; // Para verificación y limpieza

    private Usuario usuarioDePruebaCreado; // Para guardar la referencia y limpiar después

    @BeforeEach
    void setUp() {
        // --- MEJORA: Simulación de Inyección de Dependencias para la prueba ---
        // En una aplicación real, un framework como Spring o Guice haría esto.
        // Aquí lo hacemos manualmente para instanciar los servicios con sus dependencias.
        this.usuarioDAO = new UsuarioDAOImpl();
        TemaUIDAO temaDAO = new TemaUIDAOImpl();
        ConfiguracionUsuarioDAO configDAO = new ConfiguracionUsuarioDAOImpl();

        this.configService = new ConfiguracionUsuarioService(configDAO, temaDAO);
        this.usuarioService = new UsuarioService(this.usuarioDAO, this.configService);
    }

    @AfterEach
    void tearDown() {
        // --- MEJORA: Limpieza de datos después de cada prueba ---
        if (usuarioDePruebaCreado != null) {
            System.out.println("--- LIMPIANDO DATOS DE PRUEBA ---");
            // La FK en la tabla de configuración tiene ON DELETE CASCADE,
            // por lo que al eliminar el usuario, su configuración también se borrará.
            usuarioDAO.eliminar(usuarioDePruebaCreado.getId());
            System.out.println("✅ Usuario de prueba con ID " + usuarioDePruebaCreado.getId() + " eliminado.");
            usuarioDePruebaCreado = null;
        }
    }

    @Test
    @DisplayName("Debería crear un nuevo usuario y su configuración por defecto de forma transaccional")
    void deberiaCrearUsuarioYConfiguracion() {
        // --- ARRANGE (Preparar) ---
        String emailUnico = "usuario.transaccional" + System.currentTimeMillis() + "@test.com";
        String nombreUsuarioUnico = "tester.trans" + System.currentTimeMillis();
        String contrasena = "claveSegura123";
        String pais = "CO"; // Colombia

        System.out.println("--- INICIANDO PRUEBA DE CREACIÓN TRANSACCIONAL ---");
        System.out.println("Intentando registrar al usuario: " + nombreUsuarioUnico);

        // --- ACT (Actuar) ---
        // Este método ahora maneja la transacción internamente
        usuarioDePruebaCreado = usuarioService.registrarNuevoUsuario(nombreUsuarioUnico, emailUnico, contrasena, pais);

        // --- ASSERT (Verificar) ---

        // 1. Verificar que el usuario fue creado.
        assertNotNull(usuarioDePruebaCreado, "El objeto Usuario no debería ser nulo tras el registro.");
        assertTrue(usuarioDePruebaCreado.getId() > 0, "El usuario debería tener un ID > 0 asignado por la BD.");
        System.out.println("✅ Usuario registrado exitosamente con ID: " + usuarioDePruebaCreado.getId());

        // 2. Verificar que se le ha asignado una configuración por defecto.
        // Usamos el DAO directamente para la verificación post-transacción.
        Optional<ConfiguracionUsuario> configOpt = configService.buscarConfiguracionPorUsuario(usuarioDePruebaCreado.getId());
        assertTrue(configOpt.isPresent(), "El usuario debería tener una configuración por defecto creada.");
        assertEquals(1, configOpt.get().getTema().getId(), "El tema asignado debería ser el tema por defecto (ID 1).");
        System.out.println("✅ Configuración por defecto verificada.");

        System.out.println("--- PRUEBA COMPLETADA ---");
    }
}