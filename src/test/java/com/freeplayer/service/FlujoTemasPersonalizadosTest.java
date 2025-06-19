package com.freeplayer.service;

import com.freeplayer.dao.ConfiguracionUsuarioDAO;
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.dao.UsuarioDAO;
import com.freeplayer.dao.impl.ConfiguracionUsuarioDAOImpl;
import com.freeplayer.dao.impl.TemaUIDAOImpl;
import com.freeplayer.dao.impl.UsuarioDAOImpl;
import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.TemaUI;
import com.freeplayer.model.Usuario;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Prueba de integración para el flujo completo de temas personalizados (SIN LIMPIEZA)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FlujoTemasPersonalizadosTest {

    // Dependencias
    private static UsuarioService usuarioService;
    private static ConfiguracionUsuarioService configService;
    private static TemaUIService temaService;
    private static UsuarioDAO usuarioDAO;
    private static TemaUIDAO temaDAO;

    // Datos de prueba
    private static Usuario usuarioDePrueba;

    @BeforeAll
    static void setUpAll() {
        // --- Inyección de Dependencias Manual ---
        usuarioDAO = new UsuarioDAOImpl();
        temaDAO = new TemaUIDAOImpl();
        ConfiguracionUsuarioDAO configDAO = new ConfiguracionUsuarioDAOImpl();
        configService = new ConfiguracionUsuarioService(configDAO, temaDAO);
        temaService = new TemaUIService(temaDAO, configService);
        usuarioService = new UsuarioService(usuarioDAO, configService);

        // --- Crear el usuario que usaremos en todas las pruebas ---
        String email = "flujo.persistido" + System.currentTimeMillis() + "@test.com";
        String username = "flujo.persistido" + System.currentTimeMillis();
        usuarioDePrueba = usuarioService.registrarNuevoUsuario(username, email, "pass123", "MX");

        System.out.println("--- @BeforeAll: Usuario de prueba creado con ID: " + usuarioDePrueba.getId() + " ---");
    }

    /*
    // --- MÉTODO DE LIMPIEZA COMENTADO ---
    // Esta advertencia del IDE se puede ignorar, ya que fue una solicitud explícita.
    @AfterAll
    static void tearDownAll() {
        if (usuarioDePrueba != null) {
            usuarioDAO.eliminar(usuarioDePrueba.getId());
            System.out.println("--- @AfterAll: Usuario de prueba con ID: " + usuarioDePrueba.getId() + " eliminado. ---");
        }
    }
    */

    @Test
    @Order(1)
    @DisplayName("1. Debería crear un usuario con el tema por defecto (ID 1)")
    void deberiaAsignarTemaPorDefecto() {
        assertNotNull(usuarioDePrueba, "El usuario de prueba no debería ser nulo.");

        Optional<ConfiguracionUsuario> configOpt = configService.buscarConfiguracionPorUsuario(usuarioDePrueba.getId());

        // CORRECCIÓN: Se verifica primero si el Optional tiene valor.
        assertTrue(configOpt.isPresent(), "El usuario debe tener una configuración.");
        assertEquals(1, configOpt.get().getTema().getId(), "El tema inicial debe ser el por defecto (ID 1).");

        System.out.println("✅ PASO 1: Verificado. El usuario tiene el tema por defecto.");
    }

    @Test
    @Order(2)
    @DisplayName("2. Debería permitir al usuario crear un tema personalizado y asignárselo")
    void deberiaCrearYAsignarTemaPersonalizado() {
        String nombreTema = "Mi Tema Fantástico";
        String jsonConfig = "{\"color\":\"blue\"}";
        Optional<TemaUI> temaCreadoOpt = temaService.crearTemaPersonalizado(usuarioDePrueba.getId(), nombreTema, jsonConfig);

        assertTrue(temaCreadoOpt.isPresent(), "El tema personalizado se debería haber creado.");
        TemaUI temaCreado = temaCreadoOpt.get();
        assertEquals(usuarioDePrueba.getId(), temaCreado.getIdPropietario(), "El propietario del tema debe ser el usuario de prueba.");

        Optional<ConfiguracionUsuario> configActualizadaOpt = configService.buscarConfiguracionPorUsuario(usuarioDePrueba.getId());

        // CORRECCIÓN: Se verifica primero si el Optional tiene valor.
        assertTrue(configActualizadaOpt.isPresent(), "La configuración actualizada no debería estar vacía.");
        assertEquals(temaCreado.getId(), configActualizadaOpt.get().getTema().getId(), "La configuración del usuario debió actualizarse al nuevo tema.");

        System.out.println("✅ PASO 2: Verificado. El usuario creó y ahora usa su tema personalizado con ID: " + temaCreado.getId());
    }

    @Test
    @Order(3)
    @DisplayName("3. Debería listar los temas globales y los del usuario")
    void deberiaListarTemasDisponibles() {
        List<TemaUI> temasDisponibles = temaService.listarTemasDisponibles(usuarioDePrueba.getId());

        // CORRECCIÓN GRAMATICAL: "debería haber" en lugar de "deberían haber"
        assertEquals(2, temasDisponibles.size(), "Debería haber 2 temas disponibles.");
        boolean foundDefault = temasDisponibles.stream().anyMatch(t -> t.getId() == 1 && t.getIdPropietario() == null);
        boolean foundCustom = temasDisponibles.stream().anyMatch(t -> t.getIdPropietario() != null && t.getIdPropietario().equals(usuarioDePrueba.getId()));

        assertTrue(foundDefault, "La lista debe contener el tema por defecto.");
        assertTrue(foundCustom, "La lista debe contener el tema personalizado del usuario.");

        System.out.println("✅ PASO 3: Verificado. La lista de temas disponibles es correcta.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Debería eliminar un tema personalizado y restablecer el tema por defecto") // CORRECCIÓN ORTOGRÁFICA
    void deberiaEliminarTemaYRestablecerDefault() {
        List<TemaUI> temasUsuario = temaService.listarTemasDisponibles(usuarioDePrueba.getId());
        TemaUI temaAEliminar = temasUsuario.stream()
                .filter(t -> t.getIdPropietario() != null)
                .findFirst()
                .orElse(null);

        assertNotNull(temaAEliminar, "No se encontró el tema personalizado a eliminar.");

        boolean eliminado = temaService.eliminarTemaPersonalizado(temaAEliminar.getId(), usuarioDePrueba.getId());
        assertTrue(eliminado, "El tema debería haberse eliminado correctamente.");

        Optional<ConfiguracionUsuario> configFinalOpt = configService.buscarConfiguracionPorUsuario(usuarioDePrueba.getId());

        // CORRECCIÓN: Se verifica primero si el Optional tiene valor.
        assertTrue(configFinalOpt.isPresent(), "La configuración final no debería estar vacía.");
        assertEquals(1, configFinalOpt.get().getTema().getId(), "La configuración debió restablecerse al tema por defecto.");

        Optional<TemaUI> temaBorradoOpt = temaDAO.consultarPorId(temaAEliminar.getId());
        assertTrue(temaBorradoOpt.isEmpty(), "El tema personalizado ya no debería existir en la BD.");

        // CORRECCIÓN ORTOGRÁFICA
        System.out.println("✅ PASO 4: Verificado. El tema personalizado fue eliminado y la configuración se restableció.");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("PRUEBA FINALIZADA: El usuario '" + usuarioDePrueba.getNombreUsuario() + "' (ID: " + usuarioDePrueba.getId() + ") ha quedado en la base de datos para su inspección.");
    }
}