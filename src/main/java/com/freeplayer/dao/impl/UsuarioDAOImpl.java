package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.UsuarioDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);

    @Override
    public Usuario insertar(Usuario usuario, Connection conn) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_usuario, email, contrasena, pais_iso) VALUES (?, ?, ?, ?)";
        String contrasenaHasheada = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt());

        // La conexión es provista por la capa de servicio, no se gestiona aquí.
        // Se lanza SQLException para que el servicio pueda hacer rollback si es necesario.
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, contrasenaHasheada);
            pstmt.setString(4, usuario.getPaisIso());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                        usuario.setContrasena(null); // Limpiar la contraseña en texto plano
                        logger.info("Usuario pre-insertado en transacción con ID: {}", usuario.getId());
                        return usuario;
                    } else {
                        throw new SQLException("Fallo al crear usuario, no se obtuvo ID.");
                    }
                }
            }
        }
        return null; // O lanzar excepción si la inserción no devuelve filas afectadas
    }

    @Override
    public void actualizar(Usuario usuario, Connection conn) throws SQLException {
        String sql = "UPDATE usuarios SET nombre_usuario = ?, email = ?, pais_iso = ? WHERE id_usuario = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getPaisIso());
            pstmt.setInt(4, usuario.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Usuario con ID: {} pre-actualizado en transacción.", usuario.getId());
            } else {
                logger.warn("No se encontró usuario con ID: {} para actualizar en transacción.", usuario.getId());
            }
        }
    }

    // --- Métodos que gestionan su propia conexión ---
    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Usuario con ID: {} fue eliminado correctamente.", id);
            } else {
                logger.warn("No se encontró ningún usuario con ID: {} para eliminar.", id);
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar el usuario con ID: {}", id, e);
            throw new DataAccessException("No se pudo eliminar el usuario.", e);
        }
    }

    // ... los métodos de consulta (consultarPorId, consultarPorEmail, etc.) no cambian ...
    // ... y el método mapearUsuario tampoco cambia.
    // (Se omite el resto de métodos de solo lectura por brevedad, permanecen iguales)
    @Override
    public Optional<Usuario> consultarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar usuario por ID: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> consultarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar usuario por email: {}", email, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> consultarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar usuario por nombre_usuario: {}", nombreUsuario, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_usuario ";

        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al listar todos los usuarios", e);
        }
        return usuarios;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setEmail(rs.getString("email"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setPaisIso(rs.getString("pais_iso"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        return usuario;
    }
}