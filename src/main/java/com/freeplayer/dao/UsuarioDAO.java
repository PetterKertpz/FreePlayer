package com.freeplayer.dao;

import com.freeplayer.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    Usuario insertar(Usuario usuario); // Devolvemos el Usuario con el ID generado
    void actualizar(Usuario usuario);
    void eliminar(int id);

    Optional<Usuario> consultarPorId(int id);
    Optional<Usuario> consultarPorEmail(String email);
    Optional<Usuario> consultarPorNombreUsuario(String nombreUsuario);

    List<Usuario> listarTodos();
}