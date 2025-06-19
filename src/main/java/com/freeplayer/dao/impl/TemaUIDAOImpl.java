package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.exceptions.DataAccessException;
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
        // SQL MODIFICADO para incluir el nuevo campo
        String sql = "INSERT INTO temas_ui (nombre_tema, configuracion_json, id_propietario) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tema.getNombreTema());
            pstmt.setString(2, tema.getConfiguracionJson());

            // Manejar el caso de que el propietario sea nulo (tema global)
            if (tema.getIdPropietario() != null) {
                pstmt.setInt(3, tema.getIdPropietario());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

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
            throw new DataAccessException("Error al insertar tema", e);
        }
    }

    // El metodo de actualizar también debería modificarse para incluir id_propietario si fuera necesario
    @Override
    public void actualizar(TemaUI tema) {
        // Solo permitimos actualizar el nombre y la configuración, no el propietario.
        String sql = "UPDATE temas_ui SET nombre_tema = ?, configuracion_json = ? WHERE id_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
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
            throw new DataAccessException("Error al actualizar el tema", e);
        }
    }


    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM temas_ui WHERE id_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
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
            throw new DataAccessException("Error al eliminar tema", e);
        }
    }

    // METODO NUEVO/MODIFICADO: Lista temas globales Y los que pertenecen al usuario
    @Override
    public List<TemaUI> listarTemasDisponiblesParaUsuario(int idUsuario) {
        List<TemaUI> temas = new ArrayList<>();
        // El SQL ahora busca temas sin propietario (globales) O temas cuyo propietario es el usuario actual
        String sql = "SELECT * FROM temas_ui WHERE id_propietario IS NULL OR id_propietario = ?";

        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    temas.add(mapearTema(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al listar los temas de UI para el usuario {}", idUsuario, e);
            throw new DataAccessException("Error al listar temas", e);
        }
        return temas;
    }

    private TemaUI mapearTema(ResultSet rs) throws SQLException {
        TemaUI tema = new TemaUI();
        tema.setId(rs.getInt("id_tema"));
        tema.setNombreTema(rs.getString("nombre_tema"));
        tema.setConfiguracionJson(rs.getString("configuracion_json"));
        // Mapear el nuevo campo, teniendo en cuenta que puede ser NULL
        tema.setIdPropietario((Integer) rs.getObject("id_propietario"));
        return tema;
    }

    // ... (El resto de métodos como consultarPorId no cambian drásticamente pero deben mapear el nuevo campo) ...
    @Override
    public Optional<TemaUI> consultarPorId(int id) {
        String sql = "SELECT * FROM temas_ui WHERE id_tema = ?";
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
        String sql = "SELECT * FROM temas_ui WHERE nombre_tema = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Reutilizamos el método de mapeo que ya tenemos para crear el objeto
                    return Optional.of(mapearTema(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar el tema de UI con nombre: {}", nombre, e);
            throw new DataAccessException("Error consultando tema por nombre", e);
        }
        // Si no se encuentra ningún tema con ese nombre, se devuelve un Optional vacío.
        return Optional.empty();
    }
}