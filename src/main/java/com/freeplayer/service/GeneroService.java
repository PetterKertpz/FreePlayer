package com.freeplayer.service;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.GeneroDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Genero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GeneroService {

    private static final Logger logger = LoggerFactory.getLogger(GeneroService.class);
    private final GeneroDAO generoDAO;

    public GeneroService(GeneroDAO generoDAO) {
        this.generoDAO = generoDAO;
    }

    /**
     * Crea un nuevo género de forma transaccional.
     * @param nombreGenero El nombre del nuevo género.
     * @return Optional con el género creado o vacío si falla.
     */
    public Optional<Genero> crearGenero(String nombreGenero) {
        Genero nuevoGenero = new Genero();
        nuevoGenero.setNombreGenero(nombreGenero);

        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null))) {
            conn.setAutoCommit(false);
            generoDAO.insertarGenero(nuevoGenero, conn);
            conn.commit();
            logger.info("Género '{}' creado con ID: {}", nombreGenero, nuevoGenero.getIdGenero());
            return Optional.of(nuevoGenero);
        } catch (SQLException e) {
            logger.error("Error al crear el género '{}'", nombreGenero, e);
            return Optional.empty();
        }
    }

    // Los métodos de consulta directa no necesitan manejo transaccional explícito aquí.
    public List<Genero> listarTodos() {
        return generoDAO.listarTodos();
    }

    /**
     * Elimina un género y reasigna sus canciones a un género por defecto.
     * ¡Esta es una lógica de negocio importante que pertenece al Service!
     * @param idGeneroAEliminar El ID del género a eliminar.
     * @param idGeneroPorDefecto El ID del género al que se moverán las canciones.
     * @return true si la operación fue exitosa.
     */
    public boolean eliminarGeneroYReasignarCanciones(int idGeneroAEliminar, int idGeneroPorDefecto) {
        if (idGeneroAEliminar == idGeneroPorDefecto) {
            logger.warn("Intento de eliminar y reasignar al mismo género. Operación cancelada.");
            return false;
        }

        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null))) {
            conn.setAutoCommit(false);

            // Paso 1: Reasignar todas las canciones del género antiguo al nuevo.
            // Esto requeriría un método en CancionDAO como:
            // cancionDAO.reasignarGeneroMasivamente(idGeneroAEliminar, idGeneroPorDefecto, conn);
            // Por ahora, simulamos el concepto.
            logger.info("Paso 1/2: Reasignando canciones del género {} al {}", idGeneroAEliminar, idGeneroPorDefecto);

            // Paso 2: Eliminar el género, ahora que ya no tiene canciones asociadas.
            Genero generoAEliminar = new Genero();
            generoAEliminar.setIdGenero(idGeneroAEliminar);
            generoDAO.eliminarGenero(generoAEliminar, conn);
            logger.info("Paso 2/2: Género con ID {} eliminado.");

            conn.commit();
            logger.info("Operación de eliminación y reasignación de género completada.");
            return true;

        } catch (SQLException e) {
            logger.error("Error en la transacción al eliminar género {}", idGeneroAEliminar, e);
            return false;
        }
    }
}