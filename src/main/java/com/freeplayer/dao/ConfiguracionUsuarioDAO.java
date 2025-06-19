package com.freeplayer.dao;

import com.freeplayer.model.ConfiguracionUsuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface ConfiguracionUsuarioDAO {

    void insertar(ConfiguracionUsuario config, Connection conn) throws SQLException;
    void actualizar(ConfiguracionUsuario config, Connection conn) throws SQLException;
    Optional<ConfiguracionUsuario> consultarPorIdUsuario(int idUsuario);
    void reasignarTemaMasivamente(int idTemaAntiguo, int idTemaNuevo);
}