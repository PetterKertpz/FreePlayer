package com.freeplayer.service;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.AutorDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Autor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AutorService {

    private static final Logger logger = LoggerFactory.getLogger(AutorService.class);
    private final AutorDAO autorDAO;

    /**
     * Constructor para inyección de dependencias.
     * @param autorDAO La implementación de AutorDAO que se usará.
     */
    public AutorService(AutorDAO autorDAO) {
        this.autorDAO = autorDAO;
    }

    /**
     * Crea un nuevo autor en la base de datos de forma transaccional.
     * @param nombre El nombre del nuevo autor.
     * @param nacionalidad La nacionalidad del nuevo autor.
     * @return Un Optional con el autor creado, o vacío si falla la operación.
     */
    public Optional<Autor> crearAutor(String nombre, String nacionalidad) {
        Autor nuevoAutor = new Autor();
        nuevoAutor.setNombreAutor(nombre);
        nuevoAutor.setNacionalidadAutor(nacionalidad);

        // La operación es una sola inserción, pero la manejamos en una transacción
        // para mantener un patrón consistente y por si se añaden más pasos en el futuro.
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("No se pudo obtener conexión", null))) {
            conn.setAutoCommit(false); // 1. Iniciar transacción

            autorDAO.insertar(nuevoAutor, conn); // 2. Ejecutar operación DAO

            conn.commit(); // 3. Confirmar transacción
            logger.info("Autor '{}' creado exitosamente con ID: {}", nombre, nuevoAutor.getIdAutor());
            return Optional.of(nuevoAutor);

        } catch (SQLException e) {
            logger.error("Error en la transacción al crear el autor: {}", nombre, e);
            // El rollback es implícito al cerrar la conexión con error sin hacer commit.
            return Optional.empty();
        }
    }

    /**
     * Actualiza la información de un autor existente.
     * @param autor El objeto autor con el ID y los nuevos datos.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarAutor(Autor autor) {
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("No se pudo obtener conexión", null))) {
            conn.setAutoCommit(false);
            autorDAO.actualizarAutor(autor, conn);
            conn.commit();
            logger.info("Autor con ID {} actualizado correctamente.", autor.getIdAutor());
            return true;
        } catch (SQLException e) {
            logger.error("Error en la transacción al actualizar el autor con ID: {}", autor.getIdAutor(), e);
            return false;
        }
    }

    /**
     * Elimina un autor.
     * @param idAutor el ID del autor a eliminar.
     * @return true si se eliminó, false en caso contrario.
     */
     public boolean eliminarAutor(int idAutor) {
        // Asumiendo que la base de datos tiene ON DELETE CASCADE para las canciones.
        // Si no, aquí se debería añadir lógica para reasignar o eliminar canciones.
        Autor autor = new Autor();
        autor.setIdAutor(idAutor);
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("No se pudo obtener conexión", null))) {
            conn.setAutoCommit(false);
            autorDAO.eliminarAutor(autor, conn);
            conn.commit();
            logger.info("Autor con ID {} eliminado correctamente.", idAutor);
            return true;
        } catch (SQLException e) {
            logger.error("Error en la transacción al eliminar el autor con ID: {}", idAutor, e);
            return false;
        }
    }


    /**
     * Busca un autor por su ID.
     * @param id El ID del autor.
     * @return Un Optional con el autor si se encuentra.
     */
    public Optional<Autor> buscarPorId(int id) {
        return autorDAO.consultarPorId(id);
    }

    /**
     * Devuelve una lista de todos los autores.
     * @return Lista de objetos Autor.
     */
    public List<Autor> listarTodos() {
        return autorDAO.listarTodos();
    }
}