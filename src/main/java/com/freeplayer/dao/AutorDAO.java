package com.freeplayer.dao;

import com.freeplayer.model.Autor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AutorDAO {

    void insertar(Autor autor, Connection coon) throws SQLException;

    void eliminarAutor(Autor autor, Connection conn) throws SQLException;

    Autor actualizarAutor(Autor autor, Connection conn) throws SQLException;

    Optional<Autor> consultarPorId(int id);
    Optional<Autor> consultarPorNombre(String nombre);

    List<Autor> listarTodos();


}
