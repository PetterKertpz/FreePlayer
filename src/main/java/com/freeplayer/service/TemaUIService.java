package com.freeplayer.service;

import com.freeplayer.dao.ConfiguracionUsuarioDAO;
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.TemaUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TemaUIService {

    private static final Logger logger = LoggerFactory.getLogger(TemaUIService.class);
    private final TemaUIDAO temaDAO;
    private final ConfiguracionUsuarioService configService; // Necesita saber de la configuración
    private static final int ID_TEMA_POR_DEFECTO = 1;

    // Inyección de dependencias
    public TemaUIService(TemaUIDAO temaDAO, ConfiguracionUsuarioService configService) {
        this.temaDAO = temaDAO;
        this.configService = configService;
    }

    /**
     * Crea un nuevo tema personalizado para un usuario y lo establece como su tema actual.
     * @return El objeto TemaUI recién creado.
     */
    public Optional<TemaUI> crearTemaPersonalizado(int idUsuario, String nombreTema, String jsonConfig) {
        TemaUI nuevoTema = new TemaUI();
        nuevoTema.setNombreTema(nombreTema);
        nuevoTema.setConfiguracionJson(jsonConfig);
        nuevoTema.setIdPropietario(idUsuario); // Se asigna el dueño

        try {
            temaDAO.insertar(nuevoTema);
            if (nuevoTema.getId() > 0) {
                // Después de crearlo, lo asignamos como el tema actual del usuario
                configService.actualizarTemaUsuario(idUsuario, nuevoTema.getId());
                logger.info("Nuevo tema personalizado con ID {} creado y asignado al usuario {}", nuevoTema.getId(), idUsuario);
                return Optional.of(nuevoTema);
            }
        } catch (Exception e) {
            logger.error("No se pudo crear el tema personalizado para el usuario {}", idUsuario, e);
        }
        return Optional.empty();
    }

    /**
     * Elimina un tema personalizado. No se puede eliminar un tema global.
     * Si el usuario está usando el tema que va a eliminar, se le reasigna el tema por defecto.
     */
    public boolean eliminarTemaPersonalizado(int idTema, int idUsuario) {
        if (idTema == ID_TEMA_POR_DEFECTO) {
            logger.warn("Intento de eliminar el tema por defecto (ID: {}). Operación denegada.", idTema);
            return false;
        }

        Optional<TemaUI> temaOpt = temaDAO.consultarPorId(idTema);
        if (temaOpt.isEmpty()) {
            logger.warn("Intento de eliminar un tema que no existe (ID: {})", idTema);
            return false;
        }

        TemaUI tema = temaOpt.get();
        // Verificar que el usuario sea el propietario del tema
        if (tema.getIdPropietario() == null || tema.getIdPropietario() != idUsuario) {
            logger.warn("El usuario {} intentó eliminar el tema {}, pero no es el propietario.", idUsuario, idTema);
            return false;
        }

        // Verificar si el usuario está usando este tema actualmente
        Optional<ConfiguracionUsuario> configOpt = configService.buscarConfiguracionPorUsuario(idUsuario);
        if (configOpt.isPresent() && configOpt.get().getTema().getId() == idTema) {
            // Si lo está usando, reestablecer al por defecto ANTES de borrar el tema
            logger.info("El usuario {} está usando el tema a eliminar. Reestableciendo al por defecto.", idUsuario);
            configService.restaurarConfiguracionPorDefecto(idUsuario);
        }

        // Ahora sí, eliminar el tema de forma segura
        temaDAO.eliminar(idTema);
        logger.info("Tema personalizado ID {} eliminado exitosamente por el usuario {}.", idTema, idUsuario);
        return true;
    }

    public List<TemaUI> listarTemasDisponibles(int idUsuario) {
        return temaDAO.listarTemasDisponiblesParaUsuario(idUsuario);
    }
}