package com.freeplayer.service;

import com.freeplayer.dao.ConfiguracionUsuarioDAO;
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.dao.impl.ConfiguracionUsuarioDAOImpl;
import com.freeplayer.dao.impl.TemaUIDAOImpl;
import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.TemaUI;
import com.freeplayer.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ConfiguracionUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionUsuarioService.class);

    // El servicio necesita acceso a los DAOs con los que va a trabajar.
    private final ConfiguracionUsuarioDAO configDAO = new ConfiguracionUsuarioDAOImpl();
    private final TemaUIDAO temaDAO = new TemaUIDAOImpl(); // Necesario para buscar temas

    /**
     * ID del tema que se considera por defecto para todos los nuevos usuarios
     * o para aquellos cuya configuración se restaura.
     */
    private static final int ID_TEMA_POR_DEFECTO = 1;

    /**
     * Busca la configuración completa de un usuario por su ID.
     * @param idUsuario El ID del usuario.
     * @return Un Optional que contiene la configuración del usuario si existe.
     */
    public Optional<ConfiguracionUsuario> buscarConfiguracionPorUsuario(int idUsuario) {
        logger.info("Buscando configuración para el usuario con ID: {}", idUsuario);
        return configDAO.consultarPorIdUsuario(idUsuario);
    }

    /**
     * Asigna una configuración inicial a un usuario recién registrado.
     * Esta es una regla de negocio clave: todo nuevo usuario tiene un tema por defecto.
     * @param usuario El objeto Usuario recién creado (debe tener su ID).
     */
    public void asignarConfiguracionInicial(Usuario usuario) {
        if (usuario == null || usuario.getId() == 0) {
            logger.error("No se puede asignar configuración a un usuario nulo o sin ID.");
            return;
        }

        logger.info("Asignando configuración inicial para el nuevo usuario: {}", usuario.getNombreUsuario());

        // Usamos la constante en lugar de un número mágico.
        Optional<TemaUI> temaPorDefectoOpt = temaDAO.consultarPorId(ID_TEMA_POR_DEFECTO);

        if (temaPorDefectoOpt.isEmpty()) {
            logger.error("¡CRÍTICO! No se encontró el tema por defecto con ID: {}. No se puede asignar configuración.", ID_TEMA_POR_DEFECTO);
            // Aquí se podría lanzar una excepción grave, ya que es un fallo de configuración del sistema.
            return;
        }

        // Creamos el nuevo objeto de configuración
        ConfiguracionUsuario nuevaConfig = new ConfiguracionUsuario();
        nuevaConfig.setUsuario(usuario);
        nuevaConfig.setTema(temaPorDefectoOpt.get());

        // Guardamos la nueva configuración en la base de datos
        configDAO.insertar(nuevaConfig);
        logger.info("Configuración inicial asignada correctamente al usuario: {}", usuario.getNombreUsuario());
    }

    /**
     * Permite a un usuario cambiar su tema de interfaz.
     * @param idUsuario El ID del usuario que cambia el tema.
     * @param idNuevoTema El ID del nuevo tema seleccionado.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarTemaUsuario(int idUsuario, int idNuevoTema) {
        Optional<ConfiguracionUsuario> configExistenteOpt = configDAO.consultarPorIdUsuario(idUsuario);
        Optional<TemaUI> nuevoTemaOpt = temaDAO.consultarPorId(idNuevoTema);

        if (configExistenteOpt.isEmpty()) {
            logger.warn("Se intentó actualizar el tema para un usuario sin configuración existente. ID: {}", idUsuario);
            return false;
        }

        if (nuevoTemaOpt.isEmpty()) {
            logger.warn("Se intentó actualizar a un tema que no existe. ID de tema: {}", idNuevoTema);
            return false;
        }

        ConfiguracionUsuario configParaActualizar = configExistenteOpt.get();
        configParaActualizar.setTema(nuevoTemaOpt.get());

        configDAO.actualizar(configParaActualizar);
        logger.info("El tema del usuario con ID: {} fue actualizado al tema con ID: {}", idUsuario, idNuevoTema);
        return true;
    }

    /**
     * Restaura la configuración de un usuario a su estado por defecto.
     * En este caso, simplemente actualiza su tema al tema predeterminado.
     * @param idUsuario El ID del usuario cuya configuración se va a restaurar.
     * @return true si la restauración fue exitosa, false en caso contrario.
     */
    public boolean restaurarConfiguracionPorDefecto(int idUsuario) {
        logger.info("Solicitud para restaurar la configuración por defecto del usuario ID: {}", idUsuario);
        // Reutilizamos la lógica de actualización, pasándole el ID del tema por defecto.
        return actualizarTemaUsuario(idUsuario, ID_TEMA_POR_DEFECTO);
    }
}