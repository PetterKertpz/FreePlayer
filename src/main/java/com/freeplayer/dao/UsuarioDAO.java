package com.freeplayer.dao;

import com.freeplayer.model.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {

    /**
     * Inserta un usuario dentro de una transacción existente.
     * @param usuario El objeto usuario a insertar.
     * @param conn La conexión SQL activa para la transacción.
     * @return El Usuario con el ID generado.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    Usuario insertar(Usuario usuario, Connection conn) throws SQLException;

    /**
     * Actualiza un usuario dentro de una transacción existente.
     * @param usuario El objeto usuario a actualizar.
     * @param conn La conexión SQL activa para la transacción.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    void actualizar(Usuario usuario, Connection conn) throws SQLException;

    // Métodos de eliminación y consulta no necesitan una transacción externa obligatoriamente,
    // por lo que mantienen su firma original. La gestión de conexión se hará internamente.
    void eliminar(int id);
    Optional<Usuario> consultarPorId(int id);
    Optional<Usuario> consultarPorEmail(String email);
    Optional<Usuario> consultarPorNombreUsuario(String nombreUsuario);
    List<Usuario> listarTodos();
}