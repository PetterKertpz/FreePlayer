package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.ListaReproduccionDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.ListaReproduccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListaReproduccionDAOImpl implements ListaReproduccionDAO {

    private static final Logger logger = LoggerFactory.getLogger(ListaReproduccionDAOImpl.class);

    @Override
    public void insertarListaReproduccion(ListaReproduccion lista, Connection conn) throws SQLException {
        String sql = "INSERT INTO listas_reproduccion (id_usuario, nombre_lista, fecha_creacion) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, lista.getIdUsuario());
            pstmt.setString(2, lista.getNombreLista());
            pstmt.setTimestamp(3, Timestamp.valueOf(lista.getFechaCreacion()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        lista.setIdlista(rs.getInt(1));
                        logger.info("Lista de reproducción '{}' pre-insertada en transacción con ID: {}", lista.getNombreLista(), lista.getIdlista());
                    }
                }
            } else {
                throw new SQLException("No se pudo crear la lista de reproducción.");
            }
        }
    }

    @Override
    public void eliminarListaReproduccion(ListaReproduccion lista, Connection conn) throws SQLException {
        String sql = "DELETE FROM listas_reproduccion WHERE id_lista = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lista.getIdlista());
            int affectedRows = pstmt.executeUpdate();
             if (affectedRows > 0) {
                logger.info("Lista con ID: {} pre-eliminada en transacción.", lista.getIdlista());
            } else {
                logger.warn("No se encontró lista con ID: {} para eliminar en transacción.", lista.getIdlista());
            }
        }
    }

    @Override
    public ListaReproduccion actualizarListaReproduccion(ListaReproduccion lista, Connection conn) throws SQLException {
        String sql = "UPDATE listas_reproduccion SET nombre_lista = ? WHERE id_lista = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lista.getNombreLista());
            pstmt.setInt(2, lista.getIdlista());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Lista de reproducción con ID: {} pre-actualizada en transacción.", lista.getIdlista());
            } else {
                logger.warn("No se encontró lista con ID: {} para actualizar en transacción.", lista.getIdlista());
            }
        }
        return lista;
    }

    @Override
    public Optional<ListaReproduccion> consultarPorId(int id) {
        String sql = "SELECT * FROM listas_reproduccion WHERE id_lista = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearListaReproduccion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar lista de reproducción por ID: {}", id, e);
            throw new DataAccessException("Error al consultar lista por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ListaReproduccion> consultarPorNombre(String nombre) {
        String sql = "SELECT * FROM listas_reproduccion WHERE nombre_lista = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearListaReproduccion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar lista de reproducción por nombre: {}", nombre, e);
            throw new DataAccessException("Error al consultar lista por nombre", e);
        }
        return Optional.empty();
    }


    @Override
    public List<ListaReproduccion> listarTodas() {
        List<ListaReproduccion> listas = new ArrayList<>();
        String sql = "SELECT * FROM listas_reproduccion ORDER BY nombre_lista";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listas.add(mapearListaReproduccion(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al listar las listas de reproducción", e);
            throw new DataAccessException("Error al listar listas de reproducción", e);
        }
        return listas;
    }

    private ListaReproduccion mapearListaReproduccion(ResultSet rs) throws SQLException {
        ListaReproduccion lista = new ListaReproduccion();
        lista.setIdlista(rs.getInt("id_lista"));
        lista.setIdUsuario(rs.getInt("id_usuario"));
        lista.setNombreLista(rs.getString("nombre_lista"));
        lista.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        return lista;
    }
}