package com.freeplayer.main;

import com.freeplayer.dao.*;
import com.freeplayer.dao.impl.*;
import com.freeplayer.model.*;
import com.freeplayer.service.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Clase principal para probar de forma integral todas las funcionalidades del sistema FreePlayer.
 * Esta clase simula un punto de entrada de la aplicación, inicializa todos los servicios
 * y ejecuta pruebas de Crear, Leer, Actualizar y Eliminar (CRUD) para cada entidad.
 *
 * IMPORTANTE: Antes de ejecutar, asegúrate de que tu base de datos 'freeplayer' esté creada
 * y que el archivo 'config.properties' tenga las credenciales correctas.
 */
public class TestUniversal {

    public static void main(String[] args) {
        System.out.println("🚀 INICIANDO PRUEBA INTEGRAL DE FREEPLAYER 🚀");

        // --- 1. INYECCIÓN MANUAL DE DEPENDENCIAS ---
        // En una aplicación real, un framework como Spring se encargaría de esto automáticamente.
        // Aquí, creamos las instancias de los DAO primero, porque los Services los necesitan.
        System.out.println("\n🔧 Inicializando DAOs y Services...");
        AutorDAO autorDAO = new AutorDAOImpl();
        GeneroDAO generoDAO = new GeneroDAOImpl();
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        TemaUIDAO temaDAO = new TemaUIDAOImpl();
        ConfiguracionUsuarioDAO configDAO = new ConfiguracionUsuarioDAOImpl();
        CancionDAO cancionDAO = new CancionDAOImpl();
        ListaReproduccionDAO listaDAO = new ListaReproduccionDAOImpl();

        // Ahora creamos las instancias de los Services, pasándoles los DAOs que necesitan.
        AutorService autorService = new AutorService(autorDAO);
        GeneroService generoService = new GeneroService(generoDAO);
        ConfiguracionUsuarioService configService = new ConfiguracionUsuarioService(configDAO, temaDAO);
        UsuarioService usuarioService = new UsuarioService(usuarioDAO, configService);
        TemaUIService temaService = new TemaUIService(temaDAO, configService);
        CancionService cancionService = new CancionService(cancionDAO, autorDAO, generoDAO);
        ListaReproduccionService listaService = new ListaReproduccionService(listaDAO);
        System.out.println("✅ Servicios inicializados correctamente.");

        // --- 2. EJECUCIÓN DE LAS PRUEBAS ---
        // Ejecutamos las pruebas en un orden lógico. Por ejemplo, primero creamos
        // autores y géneros, que son necesarios para crear canciones.
        probarAutores(autorService);
        probarGeneros(generoService);
        probarUsuariosYTemas(usuarioService, temaService, configService);
        probarCanciones(cancionService, autorService, generoService);
        probarListasDeReproduccion(listaService, usuarioService);


        System.out.println("\n🎉 PRUEBA INTEGRAL FINALIZADA 🎉");
    }

    /**
     * Prueba el flujo completo (CRUD) para la entidad Autor.
     */
    private static void probarAutores(AutorService autorService) {
        System.out.println("\n--- 🧪 INICIANDO PRUEBAS DE AUTORES ---");

        // CREATE
        System.out.println("1. Creando un nuevo autor...");
        Optional<Autor> nuevoAutorOpt = autorService.crearAutor("Jorge Drexler", "UY");
        if (nuevoAutorOpt.isPresent()) {
            Autor nuevoAutor = nuevoAutorOpt.get();
            System.out.println("   ✅ Autor creado: " + nuevoAutor);

            // READ (by ID)
            System.out.println("2. Buscando autor por ID...");
            Optional<Autor> autorConsultadoOpt = autorService.buscarPorId(nuevoAutor.getIdAutor());
            assertTrue(autorConsultadoOpt.isPresent(), "El autor debería existir tras ser creado.");
            System.out.println("   ✅ Autor encontrado: " + autorConsultadoOpt.get());

            // UPDATE
            System.out.println("3. Actualizando autor...");
            autorConsultadoOpt.get().setNombreAutor("Jorge Drexler (Actualizado)");
            boolean actualizado = autorService.actualizarAutor(autorConsultadoOpt.get());
            assertTrue(actualizado, "La actualización del autor debería ser exitosa.");
            System.out.println("   ✅ Autor actualizado.");

            // READ (List all)
            System.out.println("4. Listando todos los autores...");
            List<Autor> autores = autorService.listarTodos();
            System.out.println("   ✅ Autores encontrados en la BD: " + autores.size());
            autores.forEach(a -> System.out.println("      - " + a));

            // DELETE
            System.out.println("5. Eliminando autor...");
            boolean eliminado = autorService.eliminarAutor(nuevoAutor.getIdAutor());
            assertTrue(eliminado, "La eliminación del autor debería ser exitosa.");
            System.out.println("   ✅ Autor eliminado.");

            // VERIFY DELETION
            Optional<Autor> autorEliminadoOpt = autorService.buscarPorId(nuevoAutor.getIdAutor());
            assertFalse(autorEliminadoOpt.isPresent(), "El autor ya no debería existir.");
            System.out.println("   ✅ Verificación de eliminación correcta.");

        } else {
            System.out.println("   ❌ Falló la creación del autor.");
        }
        System.out.println("--- 🏁 PRUEBAS DE AUTORES FINALIZADAS ---");
    }

    /**
     * Prueba el flujo completo (CRUD) para la entidad Genero.
     */
    private static void probarGeneros(GeneroService generoService) {
         System.out.println("\n--- 🧪 INICIANDO PRUEBAS DE GÉNEROS ---");
        // CREATE
        System.out.println("1. Creando un nuevo género...");
        Optional<Genero> nuevoGeneroOpt = generoService.crearGenero("Rock Alternativo");
        if (nuevoGeneroOpt.isPresent()) {
            Genero nuevoGenero = nuevoGeneroOpt.get();
            System.out.println("   ✅ Género creado: " + nuevoGenero);
            // El resto de las pruebas (actualizar, eliminar) requerirían añadir métodos al servicio.
            // Por ahora, solo probamos la creación y el listado.
        } else {
             System.out.println("   ❌ Falló la creación del género.");
        }

        // READ (List all)
        System.out.println("2. Listando todos los géneros...");
        List<Genero> generos = generoService.listarTodos();
        System.out.println("   ✅ Géneros encontrados en la BD: " + generos.size());
        generos.forEach(g -> System.out.println("      - " + g));
         System.out.println("--- 🏁 PRUEBAS DE GÉNEROS FINALIZADAS ---");
    }

    /**
     * Prueba el flujo de registro de usuarios, creación de temas y asignación.
     */
    private static void probarUsuariosYTemas(UsuarioService usuarioService, TemaUIService temaService, ConfiguracionUsuarioService configService) {
        System.out.println("\n--- 🧪 INICIANDO PRUEBAS DE USUARIOS Y TEMAS ---");

        // CREATE (Registro transaccional)
        System.out.println("1. Registrando un nuevo usuario (esto crea el usuario y su config. por defecto)...");
        String email = "test.user." + System.currentTimeMillis() + "@example.com";
        Usuario nuevoUsuario = usuarioService.registrarNuevoUsuario("tester", email, "pass123", "CO");
        assertNotNull(nuevoUsuario, "El usuario no debería ser nulo tras el registro.");
        System.out.println("   ✅ Usuario registrado: " + nuevoUsuario);

        // VERIFY default configuration
        System.out.println("2. Verificando configuración por defecto...");
        Optional<ConfiguracionUsuario> configOpt = configService.buscarConfiguracionPorUsuario(nuevoUsuario.getId());
        assertTrue(configOpt.isPresent(), "El usuario debería tener una configuración.");
        assertEquals(1, configOpt.get().getTema().getId(), "El tema por defecto (ID 1) debería estar asignado.");
        System.out.println("   ✅ Configuración por defecto asignada correctamente.");

        // CREATE custom theme
        System.out.println("3. Creando un tema personalizado para el usuario...");
        String nombreTema = "Mi Tema Oscuro";
        String jsonConfig = "{\"background\": \"#121212\", \"color\": \"#FFFFFF\"}";
        Optional<TemaUI> temaCreadoOpt = temaService.crearTemaPersonalizado(nuevoUsuario.getId(), nombreTema, jsonConfig);
        assertTrue(temaCreadoOpt.isPresent(), "El tema personalizado debería haberse creado.");
        System.out.println("   ✅ Tema personalizado creado: " + temaCreadoOpt.get());

        // VERIFY theme update
        System.out.println("4. Verificando que el nuevo tema está activo...");
        configOpt = configService.buscarConfiguracionPorUsuario(nuevoUsuario.getId());
        assertEquals(temaCreadoOpt.get().getId(), configOpt.get().getTema().getId(), "El tema activo debería ser el recién creado.");
        System.out.println("   ✅ El tema del usuario se actualizó correctamente.");

        // LIST available themes for user
        System.out.println("5. Listando temas disponibles para el usuario (globales + propios)...");
        List<TemaUI> temasDisponibles = temaService.listarTemasDisponibles(nuevoUsuario.getId());
        System.out.println("   ✅ Temas disponibles: " + temasDisponibles.size());
        temasDisponibles.forEach(t -> System.out.println("      - " + t.getNombreTema() + " (Propietario: " + t.getIdPropietario() + ")"));

        // DELETE custom theme
        System.out.println("6. Eliminando el tema personalizado...");
        boolean eliminado = temaService.eliminarTemaPersonalizado(temaCreadoOpt.get().getId(), nuevoUsuario.getId());
        assertTrue(eliminado, "El tema personalizado debería eliminarse.");
        System.out.println("   ✅ Tema personalizado eliminado.");

        // VERIFY theme reset to default
        System.out.println("7. Verificando que el tema se restableció al por defecto...");
        configOpt = configService.buscarConfiguracionPorUsuario(nuevoUsuario.getId());
        assertEquals(1, configOpt.get().getTema().getId(), "El tema debería haberse restablecido al por defecto.");
        System.out.println("   ✅ Tema restablecido correctamente.");

        // DELETE user
        System.out.println("8. Eliminando usuario...");
        usuarioService.eliminarUsuario(nuevoUsuario.getId()); // Asume que ON DELETE CASCADE funciona
        Optional<Usuario> usuarioEliminadoOpt = usuarioService.buscarPorEmail(email);
        assertFalse(usuarioEliminadoOpt.isPresent(), "El usuario ya no debería existir.");
        System.out.println("   ✅ Usuario eliminado correctamente.");

        System.out.println("--- 🏁 PRUEBAS DE USUARIOS Y TEMAS FINALIZADAS ---");
    }

    /**
     * Prueba la creación y consulta de canciones.
     * Asume que ya existen autores y géneros.
     */
    private static void probarCanciones(CancionService cancionService, AutorService autorService, GeneroService generoService) {
        System.out.println("\n--- 🧪 INICIANDO PRUEBAS DE CANCIONES ---");

        // SETUP: Necesitamos un autor y un género para crear una canción.
        System.out.println("1. Creando autor y género de prueba para la canción...");
        Autor autor = autorService.crearAutor("Fito Paez", "AR").orElse(null);
        Genero genero = generoService.crearGenero("Trova").orElse(null);
        assertNotNull(autor, "Se necesita un autor para la prueba de canciones.");
        assertNotNull(genero, "Se necesita un género para la prueba de canciones.");

        // CREATE
        System.out.println("2. Creando una nueva canción...");
        Cancion nuevaCancion = new Cancion();
        nuevaCancion.setNombreCancion("11 y 6");
        nuevaCancion.setIdautor(autor.getIdAutor());
        nuevaCancion.setIdgenero(genero.getIdGenero());
        nuevaCancion.setDuracion(Duration.ofMinutes(3).plusSeconds(45));
        nuevaCancion.setUrl("http://example.com/fito.mp3");
        nuevaCancion.setUrlMiniatura("http://example.com/fito.jpg");
        nuevaCancion.setFechaPublicacion(LocalDateTime.now());
        // El idalbum y idListaReproduccion no son parte directa del constructor transaccional, se manejarían aparte.

        // NOTA: Tu `CancionService` actual no tiene un método `crearCancion` completo.
        // Lo ideal sería tenerlo. Por ahora, no podemos probar la creación.
        // Optional<Cancion> cancionCreadaOpt = cancionService.crearCancion(nuevaCancion);
        System.out.println("   ⚠️  ADVERTENCIA: El método `CancionService.crearCancion` no está completamente implementado. Saltando prueba de creación.");

        // READ (List all)
        System.out.println("3. Listando todas las canciones existentes...");
        List<Cancion> canciones = cancionService.listarTodas();
        System.out.println("   ✅ Canciones encontradas en la BD: " + canciones.size());
        canciones.forEach(c -> System.out.println("      - " + c));

        // CLEANUP
        System.out.println("4. Limpiando autor y género de prueba...");
        autorService.eliminarAutor(autor.getIdAutor());
        // Para eliminar el género necesitaríamos una lógica de reasignación como en tu service.
        System.out.println("   ✅ Autor de prueba eliminado.");
        System.out.println("--- 🏁 PRUEBAS DE CANCIONES FINALIZADAS ---");
    }

     /**
     * Prueba la creación y consulta de listas de reproducción.
     */
    private static void probarListasDeReproduccion(ListaReproduccionService listaService, UsuarioService usuarioService) {
        System.out.println("\n--- 🧪 INICIANDO PRUEBAS DE LISTAS DE REPRODUCCIÓN ---");

        // SETUP: Necesitamos un usuario
        System.out.println("1. Creando usuario de prueba para la lista...");
        String email = "list.user." + System.currentTimeMillis() + "@example.com";
        Usuario usuario = usuarioService.registrarNuevoUsuario("list-tester", email, "pass123", "MX");
        assertNotNull(usuario, "Se necesita un usuario para la prueba de listas.");

        // CREATE
        System.out.println("2. Creando una nueva lista de reproducción...");
        Optional<ListaReproduccion> listaOpt = listaService.crearListaReproduccion(usuario, "Mis Éxitos");
        assertTrue(listaOpt.isPresent(), "La lista de reproducción debería crearse.");
        System.out.println("   ✅ Lista creada: " + listaOpt.get());

        // DELETE
        System.out.println("3. Eliminando la lista de reproducción...");
        boolean eliminada = listaService.eliminarLista(listaOpt.get().getIdlista());
        assertTrue(eliminada, "La lista debería eliminarse.");
        System.out.println("   ✅ Lista eliminada.");

        // CLEANUP
        System.out.println("4. Limpiando usuario de prueba...");
        usuarioService.eliminarUsuario(usuario.getId());
        System.out.println("   ✅ Usuario de prueba eliminado.");

        System.out.println("--- 🏁 PRUEBAS DE LISTAS DE REPRODUCCIÓN FINALIZADAS ---");
    }


    // --- MÉTODOS DE UTILIDAD PARA LAS ASERCIONES ---
    // En una clase Main, no tenemos las aserciones de JUnit, así que creamos métodos simples
    // para verificar condiciones y lanzar una excepción si algo falla, deteniendo la prueba.

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FALLO EN LA PRUEBA: " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("FALLO EN LA PRUEBA: " + message);
        }
    }

     private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("FALLO EN LA PRUEBA: " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("FALLO EN LA PRUEBA: " + message + ". Se esperaba <" + expected + "> pero se obtuvo <" + actual + ">");
        }
    }
}