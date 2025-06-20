package com.freeplayer.dao;

import com.freeplayer.model.Genero;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

public interface GeneroDAO {
    void insertarGenero(Genero genero, Connection conn) throws SQLException;

    void eliminarGenero(Genero genero, Connection conn) throws SQLException;

    Genero actualizarGenero(Genero genero, Connection conn) throws SQLException;

    Optional<Genero> consultarPorId(int id, Connection conn) throws SQLException;

    Optional<Genero> consultarPorNombre(String nombre, Connection conn) throws SQLException;

    List<Genero> listarTodos();
}
