package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.ConfiguracionUsuarioDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.TemaUI;
import com.freeplayer.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class ConfiguracionUsuarioDAOImpl implements ConfiguracionUsuarioDAO {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionUsuarioDAOImpl.class);

    @Override
    public void insertar(ConfiguracionUsuario config, Connection conn) throws SQLException {
        String sql = "INSERT INTO configuraciones_usuario (id_usuario, id_tema) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, config.getUsuario().getId());
            pstmt.setInt(2, config.getTema().getId());
            pstmt.executeUpdate();
            logger.info("Configuración pre-insertada en transacción para usuario ID: {}", config.getUsuario().getId());
        }
    }

    @Override
    public void actualizar(ConfiguracionUsuario config, Connection conn) throws SQLException {
        String sql = "UPDATE configuraciones_usuario SET id_tema = ? WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, config.getTema().getId());
            pstmt.setInt(2, config.getUsuario().getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Optional<ConfiguracionUsuario> consultarPorIdUsuario(int idUsuario) {
        String sql = "SELECT cu.id_configuracion, " +
                "u.id_usuario, u.nombre_usuario, u.email, u.pais_iso, u.fecha_registro, " +
                "t.id_tema, t.nombre_tema, t.configuracion_json " +
                "FROM configuraciones_usuario cu " +
                "JOIN usuarios u ON cu.id_usuario = u.id_usuario " +
                "JOIN temas_ui t ON cu.id_tema = t.id_tema " +
                "WHERE cu.id_usuario = ?";

        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearConfiguracionDesdeResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar la configuración para el usuario con ID: {}", idUsuario, e);
            throw new DataAccessException("Error al consultar configuración", e);
        }
        return Optional.empty();
    }

    // ... el resto de los métodos permanece igual
    @Override
    public void reasignarTemaMasivamente(int idTemaAntiguo, int idTemaNuevo) {
        String sql = "UPDATE configuraciones_usuario SET id_tema = ? WHERE id_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
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
            throw new DataAccessException("Error en reasignación masiva de temas", e);
        }
    }

    // --- MEJORA: Métodos de mapeo privados para reducir duplicación ---
    private ConfiguracionUsuario mapearConfiguracionDesdeResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = mapearUsuarioDesdeJoin(rs);
        TemaUI tema = mapearTemaDesdeJoin(rs);

        ConfiguracionUsuario config = new ConfiguracionUsuario();
        config.setId(rs.getInt("id_configuracion"));
        config.setUsuario(usuario);
        config.setTema(tema);
        return config;
    }

    private Usuario mapearUsuarioDesdeJoin(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPaisIso(rs.getString("pais_iso"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        return usuario;
    }

    private TemaUI mapearTemaDesdeJoin(ResultSet rs) throws SQLException {
        TemaUI tema = new TemaUI();
        tema.setId(rs.getInt("id_tema"));
        tema.setNombreTema(rs.getString("nombre_tema"));
        tema.setConfiguracionJson(rs.getString("configuracion_json"));
        return tema;
    }
}