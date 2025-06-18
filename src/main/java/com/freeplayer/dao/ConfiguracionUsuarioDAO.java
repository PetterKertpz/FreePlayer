package com.freeplayer.dao;

import com.freeplayer.model.ConfiguracionUsuario;
import java.util.Optional;

public interface ConfiguracionUsuarioDAO {

    void insertar(ConfiguracionUsuario config);
    void actualizar(ConfiguracionUsuario config);
    Optional<ConfiguracionUsuario> consultarPorIdUsuario(int idUsuario);

    /**
     * Reasigna todos los usuarios que usan un tema específico a otro tema.
     * Útil para cuando un tema se va a eliminar.
     * @param idTemaAntiguo El ID del tema que se va a reemplazar.
     * @param idTemaNuevo El ID del nuevo tema que se asignará.
     */
    void reasignarTemaMasivamente(int idTemaAntiguo, int idTemaNuevo); // <-- AÑADIR ESTA LÍNEA
}