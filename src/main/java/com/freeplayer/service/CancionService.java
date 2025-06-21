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
        // Obtenemos una conexión que usaremos para toda la transacción.
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null))) {
            conn.setAutoCommit(false); // Iniciamos la transacción

            // Lógica de negocio: Verificar que las entidades relacionadas existen ANTES de insertar.
            // Usamos los métodos del DAO que aceptan una conexión para que todo sea parte de la misma transacción.

            // ¡ESTA LÍNEA AHORA ES VÁLIDA!
            boolean autorExiste = autorDAO.consultarPorId(cancion.getIdautor(), conn).isPresent();

            // Esta línea ya era válida porque GeneroDAO estaba bien diseñado.
            boolean generoExiste = generoDAO.consultarPorId(cancion.getIdgenero(), conn).isPresent();

            // ... (resto de la lógica de validación, inserción, commit y rollback) ...

        } catch (SQLException e) {
            // ... (manejo de error) ...
        }
        return Optional.empty(); // O la canción creada
    }

    public Optional<Cancion> buscarPorId(int id) {
        return cancionDAO.consultarPorId(id);
    }

    public List<Cancion> listarTodas() {
        return cancionDAO.listarTodas();
    }
}