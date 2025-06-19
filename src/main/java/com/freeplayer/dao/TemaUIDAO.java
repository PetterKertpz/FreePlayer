package com.freeplayer.dao;

import com.freeplayer.model.TemaUI;
import java.util.List;
import java.util.Optional;

public interface TemaUIDAO {

    void insertar(TemaUI tema); // Insertar ahora también guardará el propietario
    void actualizar(TemaUI tema);
    void eliminar(int id);
    Optional<TemaUI> consultarPorId(int id);
    Optional<TemaUI> consultarPorNombre(String nombre);

    // METODO MODIFICADO: Ya no es un "listarTodos" genérico.
    List<TemaUI> listarTemasDisponiblesParaUsuario(int idUsuario);
}