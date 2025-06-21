package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.GeneroDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Genero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GeneroDAOImpl implements GeneroDAO {

    private static final Logger logger = LoggerFactory.getLogger(GeneroDAOImpl.class);

    @Override
    public void insertarGenero(Genero genero, Connection conn) throws SQLException {
        String sql = "INSERT INTO generos (nombre_genero) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, genero.getNombreGenero());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        genero.setIdGenero(rs.getInt(1));
                        logger.info("Género pre-insertado en transacción con ID: {}", genero.getIdGenero());
                    }
                }
            } else {
                throw new SQLException("No se pudo insertar el género, no se generó ID.");
            }
        }
    }

    @Override
    public void eliminarGenero(Genero genero, Connection conn) throws SQLException {
        String sql = "DELETE FROM generos WHERE id_genero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, genero.getIdGenero());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Género con ID: {} pre-eliminado en transacción.", genero.getIdGenero());
            } else {
                logger.warn("No se encontró género con ID: {} para eliminar en transacción.", genero.getIdGenero());
            }
        }
    }

    @Override
    public Genero actualizarGenero(Genero genero, Connection conn) throws SQLException {
        String sql = "UPDATE generos SET nombre_genero = ? WHERE id_genero = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, genero.getNombreGenero());
            pstmt.setInt(2, genero.getIdGenero());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Género con ID: {} pre-actualizado en transacción.", genero.getIdGenero());
            } else {
                logger.warn("No se encontró género con ID: {} para actualizar en transacción.", genero.getIdGenero());
            }
        }
        return genero;
    }

    // Para los métodos de consulta, como la interfaz los pide con conexión, los implementamos así.
    // Esto es útil si quieres, por ejemplo, consultar varios géneros dentro de una misma transacción más grande.
    @Override
public Optional<Genero> consultarPorId(int id, Connection conn) throws SQLException {
    String sql = "SELECT * FROM generos WHERE id_genero = ?";
    // ELIMINAR ESTE BLOQUE TRY-WITH-RESOURCES QUE CREA UNA NUEVA CONEXIÓN
    // try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
    //      PreparedStatement pstmt = conn.prepareStatement(sql)) {

    // USAR DIRECTAMENTE LA CONEXIÓN RECIBIDA
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapearGenero(rs));
            }
        }
    }
    return Optional.empty();
}

    @Override
    public Optional<Genero> consultarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM generos WHERE nombre_genero = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearGenero(rs));
                }
            }
        }
        return Optional.empty();
    }

    // El metodo listarTodos no requiere una transacción externa, por lo que gestiona su propia conexión.
    @Override
    public List<Genero> listarTodos() {
        List<Genero> generos = new ArrayList<>();
        String sql = "SELECT * FROM generos ORDER BY nombre_genero";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                generos.add(mapearGenero(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al listar todos los géneros", e);
            throw new DataAccessException("No se pudieron listar los géneros.", e);
        }
        return generos;
    }

    private Genero mapearGenero(ResultSet rs) throws SQLException {
        Genero genero = new Genero();
        genero.setIdGenero(rs.getInt("id_genero"));
        genero.setNombreGenero(rs.getString("nombre_genero"));
        return genero;
    }
}