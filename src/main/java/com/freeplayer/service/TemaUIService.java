package com.freeplayer.service;

import com.freeplayer.dao.ConfiguracionUsuarioDAO; // Importar
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.dao.impl.ConfiguracionUsuarioDAOImpl; // Importar
import com.freeplayer.dao.impl.TemaUIDAOImpl;
import com.freeplayer.model.TemaUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TemaUIService {

    private static final Logger logger = LoggerFactory.getLogger(TemaUIService.class);
    private final TemaUIDAO temaDAO = new TemaUIDAOImpl();
    // Necesitamos acceso al DAO de configuración para reasignar usuarios
    private final ConfiguracionUsuarioDAO configDAO = new ConfiguracionUsuarioDAOImpl();
    private static final int ID_TEMA_POR_DEFECTO = 1;

    // ... (métodos existentes como crearTema, obtenerTodosLosTemas)

    /**
     * Elimina un tema por su ID. Antes de eliminarlo, reasigna a todos los
     * usuarios que lo estuvieran usando al tema por defecto.
     * No se puede eliminar el tema por defecto.
     * @param id El ID del tema a eliminar.
     */
    public void eliminarTema(int id) {
        if (id == ID_TEMA_POR_DEFECTO) {
            logger.warn("Intento de eliminar el tema por defecto (ID: {}). Operación denegada.", id);
            return; // No permitir que el tema por defecto sea eliminado.
        }

        logger.info("Iniciando proceso para eliminar tema de UI con ID: {}", id);

        // 1. Reasignar todos los usuarios que usaban este tema al tema por defecto.
        // Esta operación es crucial y debe ocurrir ANTES de borrar el tema.
        configDAO.reasignarTemaMasivamente(id, ID_TEMA_POR_DEFECTO);

        // 2. Ahora que ningún usuario depende de este tema, podemos eliminarlo de forma segura.
        temaDAO.eliminar(id);

        logger.info("Proceso de eliminación del tema ID: {} completado.", id);
    }
}