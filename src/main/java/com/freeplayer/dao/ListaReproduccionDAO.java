package com.freeplayer.dao;

import com.freeplayer.model.ListaReproduccion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ListaReproduccionDAO {

    void insertarListaReproduccion(ListaReproduccion listaReproduccion, Connection conn) throws SQLException;

    void eliminarListaReproduccion(ListaReproduccion listaReproduccion, Connection conn) throws SQLException;

    ListaReproduccion actualizarListaReproduccion(ListaReproduccion listaReproduccion, Connection conn) throws SQLException;

    Optional<ListaReproduccion> consultarPorId(int id);

    Optional<ListaReproduccion> consultarPorNombre(String nombre);

    List<ListaReproduccion> listarTodas();
}
