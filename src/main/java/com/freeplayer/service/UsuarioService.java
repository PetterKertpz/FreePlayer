package com.freeplayer.service;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.UsuarioDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    // --- MEJORA: Inyección de dependencias ---
    private final UsuarioDAO usuarioDAO;
    private final ConfiguracionUsuarioService configService;

    public UsuarioService(UsuarioDAO usuarioDAO, ConfiguracionUsuarioService configService) {
        this.usuarioDAO = usuarioDAO;
        this.configService = configService;
    }

    /**
     * Registra un nuevo usuario y su configuración en una única transacción.
     * @return El objeto Usuario creado con su ID, o null si falla el registro.
     */
    public Usuario registrarNuevoUsuario(String nombreUsuario, String email, String contrasenaEnTextoPlano, String paisIso) {
        // Primero, verificar si el usuario ya existe (esto no necesita estar en la transacción)
        if (usuarioDAO.consultarPorEmail(email).isPresent() || usuarioDAO.consultarPorNombreUsuario(nombreUsuario).isPresent()) {
            logger.warn("Intento de registro con email o nombre de usuario ya existente: {}", email);
            return null; // O lanzar una excepción específica
        }

        Connection conn = null;
        try {
            // 1. Obtener una única conexión para toda la operación
            conn = DBConnectionPool.getConnection()
                    .orElseThrow(() -> new DataAccessException("No se pudo obtener conexión del pool.", null));

            // 2. Desactivar el auto-commit para controlar la transacción manualmente
            conn.setAutoCommit(false);

            // 3. Preparar e insertar el usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setContrasena(contrasenaEnTextoPlano);
            nuevoUsuario.setPaisIso(paisIso);

            Usuario usuarioInsertado = usuarioDAO.insertar(nuevoUsuario, conn);

            // 4. Asignar la configuración inicial usando la misma conexión
            configService.asignarConfiguracionInicial(usuarioInsertado, conn);

            // 5. Si todo fue bien, confirmar la transacción (hacer los cambios permanentes)
            conn.commit();
            logger.info("Usuario y configuración insertados. Transacción confirmada para usuario ID: {}", usuarioInsertado.getId());

            return usuarioInsertado;

        } catch (SQLException | DataAccessException e) {
            logger.error("Error durante la transacción de registro de usuario. Revirtiendo cambios.", e);
            try {
                if (conn != null) {
                    // 6. Si algo falló, revertir todos los cambios hechos en esta transacción
                    conn.rollback();
                    logger.warn("Transacción revertida para el intento de registro de: {}", email);
                }
            } catch (SQLException ex) {
                logger.error("CRÍTICO: Error al intentar revertir la transacción.", ex);
            }
            // Puedes relanzar una excepción personalizada o devolver null
            return null;
        } finally {
            try {
                if (conn != null) {
                    // 7. Restaurar el modo auto-commit y devolver la conexión al pool
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión y devolverla al pool.", e);
            }
        }
    }

    public boolean verificarLogin(String email, String contrasenaEnTextoPlano) {
        Optional<Usuario> usuarioOpt = usuarioDAO.consultarPorEmail(email);

        return usuarioOpt.map(usuario ->
                BCrypt.checkpw(contrasenaEnTextoPlano, usuario.getContrasena())
        ).orElse(false);
    }

    public void eliminarUsuario(int id) {
        // La eliminación ya usa ON DELETE CASCADE, por lo que es atómica a nivel de BD.
        // No se necesita una transacción manejada en Java para este caso simple.
        usuarioDAO.eliminar(id);
        logger.info("Solicitud para eliminar usuario con ID: {}", id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.consultarPorEmail(email);
    }
}