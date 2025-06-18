package com.freeplayer.dao;
import com.freeplayer.model.TemaUI;

import java.util.List;
import java.util.Optional;

public interface TemaUIDAO {

    void insertar(TemaUI tema);
    void actualizar(TemaUI tema);
    void eliminar(int id);
    Optional<TemaUI> consultarPorId(int id);
    Optional<TemaUI> consultarPorNombre(String nombre);
    List<TemaUI> listarTodos();
}