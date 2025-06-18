package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.model.TemaUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemaUIDAOImpl implements TemaUIDAO {

    private static final Logger logger = LoggerFactory.getLogger(TemaUIDAOImpl.class);

    @Override
    public void insertar(TemaUI tema) {
        String sql = "INSERT INTO temas_ui (nombre_tema, configuracion_json) VALUES (?, ?)";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tema.getNombreTema());
            pstmt.setString(2, tema.getConfiguracionJson());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tema.setId(generatedKeys.getInt(1));
                        logger.info("Tema de UI insertado con ID: {}", tema.getId());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error al insertar el tema de UI: {}", tema.getNombreTema(), e);
        }
    }

    @Override
    public void actualizar(TemaUI tema) {
        String sql = "UPDATE temas_ui SET nombre_tema = ?, configuracion_json = ? WHERE id_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tema.getNombreTema());
            pstmt.setString(2, tema.getConfiguracionJson());
            pstmt.setInt(3, tema.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Tema de UI con ID: {} actualizado.", tema.getId());
            } else {
                logger.warn("No se encontró tema de UI con ID: {} para actualizar.", tema.getId());
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar el tema de UI con ID: {}", tema.getId(), e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM temas_ui WHERE id_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Tema de UI con ID: {} eliminado.", id);
            } else {
                logger.warn("No se encontró tema de UI con ID: {} para eliminar.", id);
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar el tema de UI con ID: {}", id, e);
        }
    }

    @Override
    public Optional<TemaUI> consultarPorId(int id) {
        String sql = "SELECT id_tema, nombre_tema, configuracion_json FROM temas_ui WHERE id_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearTema(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar el tema de UI con ID: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TemaUI> consultarPorNombre(String nombre) {
        String sql = "SELECT id_tema, nombre_tema, configuracion_json FROM temas_ui WHERE nombre_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearTema(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar el tema de UI con nombre: {}", nombre, e);
        }
        return Optional.empty();
    }

    @Override
    public List<TemaUI> listarTodos() {
        List<TemaUI> temas = new ArrayList<>();
        String sql = "SELECT id_tema, nombre_tema, configuracion_json FROM temas_ui";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new SQLException("Conexión nula"));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                temas.add(mapearTema(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al listar los temas de UI", e);
        }
        return temas;
    }

    private TemaUI mapearTema(ResultSet rs) throws SQLException {
        TemaUI tema = new TemaUI();
        tema.setId(rs.getInt("id_tema"));
        tema.setNombreTema(rs.getString("nombre_tema"));
        tema.setConfiguracionJson(rs.getString("configuracion_json"));
        return tema;
    }
}