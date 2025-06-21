package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.CancionDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Cancion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CancionDAOImpl implements CancionDAO {

    private static final Logger logger = LoggerFactory.getLogger(CancionDAOImpl.class);

    @Override
    public void insertarCancion(Cancion cancion, Connection conn) throws SQLException {
        String sql = "INSERT INTO canciones (nombre_cancion, id_autor, id_genero, lista_reproduccion_album, duracion," +
                " url ," +
                "ruta_portada, fecha_lanzamiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cancion.getNombreCancion());
            pstmt.setInt(2, cancion.getIdautor());
            pstmt.setInt(3, cancion.getIdgenero());
            pstmt.setInt(4, cancion.getIdalbum());
            // Guardamos la duración como segundos totales.
            pstmt.setLong(5, cancion.getDuracion().toSeconds());
            pstmt.setString(6, cancion.getUrl());
            pstmt.setString(7, cancion.getUrlMiniatura());
            pstmt.setTimestamp(8, Timestamp.valueOf(cancion.getFechaPublicacion()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cancion.setId(rs.getInt(1));
                        logger.info("Canción '{}' pre-insertada en transacción con ID: {}", cancion.getNombreCancion(), cancion.getId());
                    }
                }
            } else {
                throw new SQLException("No se pudo crear la canción.");
            }
        }
    }

    @Override
    public void eliminarCancion(Cancion cancion, Connection conn) throws SQLException {
        String sql = "DELETE FROM canciones WHERE id_cancion = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, cancion.getId());
            int affectedRows = pstmt.executeUpdate();
            if(affectedRows > 0){
                logger.info("Canción con ID: {} pre-eliminada en transacción.", cancion.getId());
            } else {
                logger.warn("No se encontró canción con ID: {} para eliminar en transacción.", cancion.getId());
            }
        }
    }

    @Override
    public Cancion actualizarCancion(Cancion cancion, Connection conn) throws SQLException {
        String sql = "UPDATE canciones SET nombre_cancion = ?, id_autor = ?, id_genero = ?, lista_reproduccion_album = ?, " +
                "duracion = ?, url = ?, ruta_portada = ?, fecha_lanzamiento = ? WHERE id_cancion = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cancion.getNombreCancion());
            pstmt.setInt(2, cancion.getIdautor());
            pstmt.setInt(3, cancion.getIdgenero());
            pstmt.setInt(4, cancion.getIdalbum());
            pstmt.setLong(5, cancion.getDuracion().toSeconds());
            pstmt.setString(6, cancion.getUrl());
            pstmt.setString(7, cancion.getUrlMiniatura());
            pstmt.setTimestamp(8, Timestamp.valueOf(cancion.getFechaPublicacion()));
            pstmt.setInt(9, cancion.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                 logger.info("Canción con ID: {} pre-actualizada en transacción.", cancion.getId());
            } else {
                logger.warn("No se encontró canción con ID: {} para actualizar en transacción.", cancion.getId());
            }
        }
        return cancion;
    }

    @Override
    public Optional<Cancion> consultarPorId(int id) {
        String sql = "SELECT * FROM canciones WHERE id_cancion = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCancion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar canción por ID: {}", id, e);
            throw new DataAccessException("Error al consultar canción por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cancion> consultarPorNombreCancion(String nombre) {
        String sql = "SELECT * FROM canciones WHERE nombre_cancion = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCancion(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar canción por nombre: {}", nombre, e);
            throw new DataAccessException("Error al consultar canción por nombre", e);
        }
        return Optional.empty();
    }


    @Override
    public List<Cancion> listarTodas() {
        List<Cancion> canciones = new ArrayList<>();
        String sql = "SELECT * FROM canciones ORDER BY nombre_cancion";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                canciones.add(mapearCancion(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al listar las canciones", e);
            throw new DataAccessException("Error al listar canciones", e);
        }
        return canciones;
    }

    private Cancion mapearCancion(ResultSet rs) throws SQLException {
        Cancion cancion = new Cancion();
        cancion.setId(rs.getInt("id_cancion"));
        cancion.setNombreCancion(rs.getString("nombre_cancion"));
        cancion.setIdautor(rs.getInt("id_autor"));
        cancion.setIdgenero(rs.getInt("id_genero"));
        cancion.setIdalbum(rs.getInt("id_album"));
        // Leemos los segundos y los convertimos a un objeto Duration.
        cancion.setDuracion(Duration.ofSeconds(rs.getLong("duracion_segundos")));
        cancion.setUrl(rs.getString("url_recurso"));
        cancion.setUrlMiniatura(rs.getString("url_miniatura"));
        cancion.setFechaPublicacion(rs.getTimestamp("fecha_publicacion").toLocalDateTime());
        // El campo 'idListaReproduccion' no está en la tabla 'canciones' según tu modelo.
        // Este campo se manejaría en una tabla intermedia (p.ej., 'listas_canciones').
        // Por ahora, lo dejamos sin mapear desde aquí.
        return cancion;
    }
}