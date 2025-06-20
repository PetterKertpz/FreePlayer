package com.freeplayer.dao;
import com.freeplayer.model.Cancion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CancionDAO {

    void insertarCancion(Cancion cancion, Connection conn) throws SQLException;

    void eliminarCancion(Cancion cancion, Connection conn) throws SQLException;

    Cancion actualizarCancion(Cancion cancion, Connection conn) throws SQLException;

    Optional<Cancion> consultarPorId(int id);

    Optional <Cancion> consultarPorNombreCancion(String nombre);

    List <Cancion> listarTodas();


}
