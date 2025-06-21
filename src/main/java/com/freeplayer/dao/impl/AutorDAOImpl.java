package com.freeplayer.dao.impl;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.AutorDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Autor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutorDAOImpl implements AutorDAO {

    private static final Logger logger = LoggerFactory.getLogger(AutorDAOImpl.class);

    /**
     * Inserta un nuevo autor en la base de datos dentro de una transacción existente.
     *
     * @param autor El objeto Autor a insertar.
     * @param conn  La conexión SQL activa para la transacción.
     * @throws SQLException si ocurre un error en la base de datos, para que el servicio pueda hacer rollback.
     */
    @Override
    public void insertar(Autor autor, Connection conn) throws SQLException {
        // La consulta SQL para insertar un nuevo autor.
        String sql = "INSERT INTO autores (nombre_autor, pais_autor_iso) VALUES (?, ?)";

        // Usamos un bloque try-with-resources para asegurar que el PreparedStatement se cierre automáticamente.
        // La conexión 'conn' es proporcionada por la capa de servicio y no se cierra aquí.
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asignamos los valores a los parámetros de la consulta.
            pstmt.setString(1, autor.getNombreAutor());
            pstmt.setString(2, autor.getNacionalidadAutor());

            // Ejecutamos la inserción.
            int affectedRows = pstmt.executeUpdate();

            // Verificamos si la inserción fue exitosa.
            if (affectedRows > 0) {
                // Obtenemos las claves generadas por la base de datos (en este caso, el ID del autor).
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Asignamos el ID generado al objeto autor.
                        autor.setIdAutor(generatedKeys.getInt(1));
                        logger.info("Autor pre-insertado en transacción con ID: {}", autor.getIdAutor());
                    } else {
                        // Si no se pudo obtener el ID, lanzamos una excepción para abortar la transacción.
                        throw new SQLException("Fallo al crear autor, no se obtuvo ID.");
                    }
                }
            }
        }
        // No capturamos SQLException aquí para que se propague hacia la capa de servicio,
        // la cual es responsable de manejar la transacción (commit/rollback).
    }

    /**
     * Elimina un autor de la base de datos. Este metodo gestiona su propia conexión.
     *
     * @param autor El objeto Autor a eliminar (se usa su ID).
     * @param conn La conexión transaccional.
     * @throws SQLException si ocurre un error.
     */
    @Override
    public void eliminarAutor(Autor autor, Connection conn) throws SQLException {
        String sql = "DELETE FROM autores WHERE id_autor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, autor.getIdAutor());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Autor con ID: {} pre-eliminado en transacción.", autor.getIdAutor());
            } else {
                logger.warn("No se encontró autor con ID: {} para eliminar en transacción.", autor.getIdAutor());
            }
        }
    }

    /**
     * Actualiza los datos de un autor en la base de datos.
     *
     * @param autor El objeto Autor con los datos actualizados.
     * @param conn La conexión transaccional.
     * @return El mismo objeto Autor, por si se quisiera encadenar operaciones.
     * @throws SQLException si ocurre un error.
     */
    @Override
    public Autor actualizarAutor(Autor autor, Connection conn) throws SQLException {
        String sql = "UPDATE autores SET nombre_autor = ?, pais_autor_iso = ? WHERE id_autor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, autor.getNombreAutor());
            pstmt.setString(2, autor.getNacionalidadAutor());
            pstmt.setInt(3, autor.getIdAutor());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Autor con ID: {} pre-actualizado en transacción.", autor.getIdAutor());
            } else {
                logger.warn("No se encontró autor con ID: {} para actualizar en transacción.", autor.getIdAutor());
            }
        }
        return autor;
    }

    /**
     * Consulta un autor por su ID. Este metodo gestiona su propia conexión.
     *
     * @param id El ID del autor a buscar.
     * @return Un Optional que contiene al autor si se encuentra, o un Optional vacío si no.
     */
    @Override
    public Optional<Autor> consultarPorId(int id) {
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null))) {
            // REFACTOR: Reutilizamos la lógica del metodo transaccional
            return this.consultarPorId(id, conn);
        } catch (SQLException e) {
            logger.error("Error al consultar autor por ID: {}", id, e);
            throw new DataAccessException("No se pudo consultar el autor por ID.", e);
        }
    }

    /**
     * Consulta un autor por su nombre.
     *
     * @param nombre El nombre del autor a buscar.
     * @return Un Optional con el autor si se encuentra.
     */
    @Override
    public Optional<Autor> consultarPorNombre(String nombre) {
        String sql = "SELECT * FROM autores WHERE nombre_autor = ?";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearAutor(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al consultar autor por nombre: {}", nombre, e);
            throw new DataAccessException("No se pudo consultar el autor por nombre.", e);
        }
        return Optional.empty();
    }

    /**
     * Lista todos los autores de la base de datos.
     *
     * @return Una lista de todos los autores.
     */
    @Override
    public List<Autor> listarTodos() {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT * FROM autores ORDER BY nombre_autor";
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null));
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                autores.add(mapearAutor(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al listar todos los autores", e);
            throw new DataAccessException("No se pudieron listar los autores.", e);
        }
        return autores;
    }

    /**
     * Metodo de utilidad privado para mapear un ResultSet a un objeto Autor.
     * Esto evita la duplicación de código en los métodos de consulta.
     *
     * @param rs El ResultSet de la consulta.
     * @return Un objeto Autor con los datos del ResultSet.
     * @throws SQLException si hay un error al acceder a los datos del ResultSet.
     */
    private Autor mapearAutor(ResultSet rs) throws SQLException {
        Autor autor = new Autor();
        autor.setIdAutor(rs.getInt("id_autor"));
        autor.setNombreAutor(rs.getString("nombre_autor"));
        autor.setNacionalidadAutor(rs.getString("nacionalidad_autor"));
        return autor;
    }

    @Override
    public Optional<Autor> consultarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM autores WHERE id_autor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearAutor(rs));
                }
            }
        }
        // La SQLException se propaga para que la capa de servicio la maneje (haciendo rollback).
        return Optional.empty();
    }
}