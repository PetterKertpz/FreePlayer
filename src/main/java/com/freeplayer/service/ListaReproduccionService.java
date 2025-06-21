package com.freeplayer.service;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.ListaReproduccionDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.ListaReproduccion;
import com.freeplayer.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ListaReproduccionService {

    private static final Logger logger = LoggerFactory.getLogger(ListaReproduccionService.class);
    private final ListaReproduccionDAO listaDAO;
    // Podría necesitar otros DAOs o Services en el futuro.
    // private final CancionDAO cancionDAO;

    public ListaReproduccionService(ListaReproduccionDAO listaDAO) {
        this.listaDAO = listaDAO;
    }

    /**
     * Crea una nueva lista de reproducción para un usuario.
     * @param usuario El usuario propietario de la lista.
     * @param nombreLista El nombre de la nueva lista.
     * @return Optional con la lista creada o vacío si falla.
     */
    public Optional<ListaReproduccion> crearListaReproduccion(Usuario usuario, String nombreLista) {
        ListaReproduccion nuevaLista = new ListaReproduccion();
        nuevaLista.setIdUsuario(usuario.getId());
        nuevaLista.setNombreLista(nombreLista);
        nuevaLista.setFechaCreacion(LocalDateTime.now());

        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null))) {
            conn.setAutoCommit(false);
            listaDAO.insertarListaReproduccion(nuevaLista, conn);
            conn.commit();
            logger.info("Lista de reproducción '{}' creada para el usuario {}", nombreLista, usuario.getNombreUsuario());
            return Optional.of(nuevaLista);
        } catch (SQLException e) {
            logger.error("Error al crear lista de reproducción para el usuario {}", usuario.getId(), e);
            return Optional.empty();
        }
    }

    public boolean eliminarLista(int idLista) {
        ListaReproduccion lista = new ListaReproduccion();
        lista.setIdlista(idLista);
        try(Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("Conexión nula", null))) {
            conn.setAutoCommit(false);

            // Lógica de negocio: Antes de borrar la lista, debemos borrar sus asociaciones con canciones.
            // Esto requeriría un DAO para la tabla intermedia (ej. lista_cancion.eliminarPorIdLista(idLista, conn))
            logger.info("Eliminando asociaciones de la lista {}", idLista);

            listaDAO.eliminarListaReproduccion(lista, conn);
            conn.commit();
            logger.info("Lista de reproducción con ID {} eliminada.", idLista);
            return true;

        } catch(SQLException e) {
            logger.error("Error al eliminar la lista de reproducción {}", idLista, e);
            return false;
        }
    }

    public Optional<ListaReproduccion> buscarPorId(int id) {
        return listaDAO.consultarPorId(id);
    }

    public List<ListaReproduccion> listarTodas() {
        return listaDAO.listarTodas();
    }

    /*
     * Lógica de Negocio Futura:
     * - public boolean agregarCancionALista(int idCancion, int idLista)
     * - public boolean quitarCancionDeLista(int idCancion, int idLista)
     * - public List<Cancion> obtenerCancionesDeLista(int idLista)
     * Estos métodos requerirían un DAO para la tabla intermedia.
     */
}