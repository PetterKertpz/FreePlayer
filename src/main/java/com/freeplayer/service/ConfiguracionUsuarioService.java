package com.freeplayer.service;

import com.freeplayer.config.DBConnectionPool;
import com.freeplayer.dao.ConfiguracionUsuarioDAO;
import com.freeplayer.dao.TemaUIDAO;
import com.freeplayer.exceptions.DataAccessException;
import com.freeplayer.model.ConfiguracionUsuario;
import com.freeplayer.model.TemaUI;
import com.freeplayer.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ConfiguracionUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionUsuarioService.class);
    private static final int ID_TEMA_POR_DEFECTO = 1;

    private final ConfiguracionUsuarioDAO configDAO;
    private final TemaUIDAO temaDAO;

    public ConfiguracionUsuarioService(ConfiguracionUsuarioDAO configDAO, TemaUIDAO temaDAO) {
        this.configDAO = configDAO;
        this.temaDAO = temaDAO;
    }

    // ... (métodos existentes como buscarConfiguracionPorUsuario, asignarConfiguracionInicial) ...

    public Optional<ConfiguracionUsuario> buscarConfiguracionPorUsuario(int idUsuario) {
        logger.info("Buscando configuración para el usuario con ID: {}", idUsuario);
        return configDAO.consultarPorIdUsuario(idUsuario);
    }

    public void asignarConfiguracionInicial(Usuario usuario, Connection conn) throws SQLException {
        if (usuario == null || usuario.getId() == 0) {
            logger.error("No se puede asignar configuración a un usuario nulo o sin ID.");
            throw new IllegalArgumentException("Usuario inválido para asignar configuración.");
        }

        logger.info("Asignando configuración inicial para el nuevo usuario: {}", usuario.getNombreUsuario());

        Optional<TemaUI> temaPorDefectoOpt = temaDAO.consultarPorId(ID_TEMA_POR_DEFECTO);

        if (temaPorDefectoOpt.isEmpty()) {
            logger.error("¡CRÍTICO! No se encontró el tema por defecto con ID: {}. No se puede asignar configuración.", ID_TEMA_POR_DEFECTO);
            throw new DataAccessException("Configuración crítica del sistema faltante: Tema por defecto no encontrado.", null);
        }

        ConfiguracionUsuario nuevaConfig = new ConfiguracionUsuario();
        nuevaConfig.setUsuario(usuario);
        nuevaConfig.setTema(temaPorDefectoOpt.get());

        configDAO.insertar(nuevaConfig, conn);
        logger.info("Configuración inicial asignada correctamente al usuario: {}", usuario.getNombreUsuario());
    }

    /**
     * Permite a un usuario cambiar su tema de interfaz.
     * @param idUsuario El ID del usuario que cambia el tema.
     * @param idNuevoTema El ID del nuevo tema seleccionado.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarTemaUsuario(int idUsuario, int idNuevoTema) {
        // Esta operación es atómica (una sola actualización), por lo que podemos dejar que el DAO maneje la conexión.
        // Para transacciones más complejas, se manejaría aquí.
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

        // Creamos la nueva configuración y la pasamos al DAO para actualizarla dentro de una transacción.
        try (Connection conn = DBConnectionPool.getConnection().orElseThrow(() -> new DataAccessException("No se pudo obtener conexión", null))) {
            conn.setAutoCommit(false); // Iniciar transacción

            ConfiguracionUsuario configParaActualizar = configExistenteOpt.get();
            configParaActualizar.setTema(nuevoTemaOpt.get());

            configDAO.actualizar(configParaActualizar, conn); // Usar el método transaccional

            conn.commit(); // Confirmar transacción
            logger.info("El tema del usuario con ID: {} fue actualizado al tema con ID: {}", idUsuario, idNuevoTema);
            return true;
        } catch(SQLException e) {
            logger.error("Error en transacción al actualizar tema para usuario {}", idUsuario, e);
            // El rollback es implícito al no hacer commit y cerrar la conexión con error.
            return false;
        }
    }

    /**
     * Restaura la configuración de un usuario a su estado por defecto.
     * @param idUsuario El ID del usuario cuya configuración se va a restaurar.
     * @return true si la restauración fue exitosa, false en caso contrario.
     */
    public boolean restaurarConfiguracionPorDefecto(int idUsuario) {
        logger.info("Solicitud para restaurar la configuración por defecto del usuario ID: {}", idUsuario);
        // Reutilizamos la lógica de actualización, pasándole el ID del tema por defecto.
        return actualizarTemaUsuario(idUsuario, ID_TEMA_POR_DEFECTO);
    }

    // --- NUEVO MÉTODO ---
    /**
     * Implementa la regla de negocio de "eliminar" una configuración.
     * En lugar de borrar el registro, lo reestablece a su valor por defecto
     * para mantener la integridad del sistema.
     * @param idUsuario El ID del usuario cuya configuración se va a reestablecer.
     * @return true si la operación fue exitosa.
     */
    public boolean eliminarOReestablecerConfiguracion(int idUsuario) {
        logger.info("Solicitud para ‘eliminar’ (restablecer) la configuración del usuario ID: {}", idUsuario);
        return restaurarConfiguracionPorDefecto(idUsuario);
    }
}