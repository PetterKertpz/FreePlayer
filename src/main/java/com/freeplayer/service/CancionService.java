package com.freeplayer.service;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.AutorDAO;
import com.freeplayer.dao.CancionDAO;
import com.freeplayer.dao.GeneroDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.Cancion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CancionService {

    private static final Logger logger = LoggerFactory.getLogger(CancionService.class);

    // Inyección de todas las dependencias necesarias
    private final CancionDAO cancionDAO;
    private final AutorDAO autorDAO;
    private final GeneroDAO generoDAO;

    public CancionService(CancionDAO cancionDAO, AutorDAO autorDAO, GeneroDAO generoDAO) {
        this.cancionDAO = cancionDAO;
        this.autorDAO = autorDAO;
        this.generoDAO = generoDAO;
    }

    /**
     * Crea una nueva canción, verificando que el autor y el género existan.
     * Toda la operación se ejecuta en una única transacción.
     *
     * @param cancion El objeto Canción con todos sus datos (excepto el ID).
     * @return Optional con la canción creada (incluyendo su nuevo ID) o vacío si falla.
     */
    public Optional<Cancion> crearCancion(Cancion cancion) {
    try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null))) {
        conn.setAutoCommit(false); // 1. Iniciar transacción

        boolean autorExiste = autorDAO.consultarPorId(cancion.getIdautor(), conn).isPresent();
        boolean generoExiste = generoDAO.consultarPorId(cancion.getIdgenero(), conn).isPresent();

        if (!autorExiste || !generoExiste) {
            logger.warn("Intento de crear canción con autor o género inexistente. Autor ID: {}, Género ID: {}", cancion.getIdautor(), cancion.getIdgenero());
            conn.rollback(); // No es estrictamente necesario ya que no se ha hecho nada, pero es buena práctica.
            return Optional.empty();
        }

        // 2. Ejecutar operación DAO
        cancionDAO.insertarCancion(cancion, conn);

        // 3. Confirmar transacción
        conn.commit();
        logger.info("Canción '{}' creada exitosamente con ID: {}", cancion.getNombreCancion(), cancion.getId());
        return Optional.of(cancion);

    } catch (SQLException e) {
        logger.error("Error en la transacción al crear la canción: {}", cancion.getNombreCancion(), e);
        // El rollback es implícito al cerrar la conexión con error, pero se puede hacer explícito.
        // No es necesario manejar el rollback aquí porque el try-with-resources cerrará la conexión
        // y si no se hizo commit, los cambios se revierten.
        return Optional.empty();
    }
}

    public Optional<Cancion> buscarPorId(int id) {
        return cancionDAO.consultarPorId(id);
    }

    public List<Cancion> listarTodas() {
        return cancionDAO.listarTodas();
    }
}