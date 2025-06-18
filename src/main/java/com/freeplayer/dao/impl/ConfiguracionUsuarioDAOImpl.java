package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.ConfiguracionUsuarioDAO;
import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.TemaUI;
import com.freeplayer.model.Usuario;

// 1. Importar las clases de SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class ConfiguracionUsuarioDAOImpl implements ConfiguracionUsuarioDAO {

    // 2. Crear la instancia del Logger para esta clase
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionUsuarioDAOImpl.class);

    @Override
    public Optional<ConfiguracionUsuario> consultarPorIdUsuario(int idUsuario) {
        String sql = "SELECT cu.id_configuracion, " +
                "u.id_usuario, u.nombre_usuario, u.email, u.pais_iso, u.fecha_registro, " +
                "t.id_tema, t.nombre_tema, t.configuracion_json " +
                "FROM configuraciones_usuario cu " +
                "JOIN usuarios u ON cu.id_usuario = u.id_usuario " +
                "JOIN temas_ui t ON cu.id_tema = t.id_tema " +
                "WHERE cu.id_usuario = ?";

        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Creamos el objeto Usuario
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPaisIso(rs.getString("pais_iso"));
                    usuario.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());

                    // Creamos el objeto TemaUI
                    TemaUI tema = new TemaUI();
                    tema.setId(rs.getInt("id_tema"));
                    tema.setNombreTema(rs.getString("nombre_tema"));
                    tema.setConfiguracionJson(rs.getString("configuracion_json"));

                    // Creamos el objeto de Configuración final
                    ConfiguracionUsuario config = new ConfiguracionUsuario();
                    config.setId(rs.getInt("id_configuracion"));
                    config.setUsuario(usuario);
                    config.setTema(tema);

                    return Optional.of(config);
                }
            }
        } catch (SQLException e) {
            // 3. Usar el logger para registrar el error
            logger.error("Error al consultar la configuración para el usuario con ID: {}", idUsuario, e);
        }
        return Optional.empty();
    }

    @Override
    public void insertar(ConfiguracionUsuario config) {
        String sql = "INSERT INTO configuraciones_usuario (id_usuario, id_tema) VALUES (?, ?)";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, config.getUsuario().getId());
            pstmt.setInt(2, config.getTema().getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error al insertar configuración para el usuario con ID: {}", config.getUsuario().getId(), e);
        }
    }

    @Override
    public void actualizar(ConfiguracionUsuario config) {
        String sql = "UPDATE configuraciones_usuario SET id_tema = ? WHERE id_usuario = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, config.getTema().getId());
            pstmt.setInt(2, config.getUsuario().getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error al actualizar configuración para el usuario con ID: {}", config.getUsuario().getId(), e);
        }
    }

    @Override
    public void reasignarTemaMasivamente(int idTemaAntiguo, int idTemaNuevo) {
        String sql = "UPDATE configuraciones_usuario SET id_tema = ? WHERE id_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTemaNuevo);
            pstmt.setInt(2, idTemaAntiguo);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Se reasignaron {} usuarios del tema ID: {} al tema ID: {}", affectedRows, idTemaAntiguo, idTemaNuevo);
            } else {
                logger.info("No había usuarios usando el tema ID: {} para reasignar.", idTemaAntiguo);
            }

        } catch (SQLException e) {
            logger.error("Error al reasignar masivamente del tema ID: {} al tema ID: {}", idTemaAntiguo, idTemaNuevo, e);
            // En una aplicación real, se podría lanzar una excepción personalizada aquí.
        }
    }
}