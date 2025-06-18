package com.freeplayer.service;

import com.freeplayer.dao.UsuarioDAO;
import com.freeplayer.dao.impl.UsuarioDAOImpl;
import com.freeplayer.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    // Es crucial que este servicio tenga una instancia del servicio de configuración.
    private final ConfiguracionUsuarioService configService = new ConfiguracionUsuarioService();

    /**
     * Registra un nuevo usuario y le asigna su configuración por defecto.
     * @return El objeto Usuario creado con su ID, o null si falla el registro.
     */
    public Usuario registrarNuevoUsuario(String nombreUsuario, String email, String contrasenaEnTextoPlano, String paisIso) {
        if (usuarioDAO.consultarPorEmail(email).isPresent() || usuarioDAO.consultarPorNombreUsuario(nombreUsuario).isPresent()) {
            logger.warn("Intento de registro con email o nombre de usuario ya existente: {}", email);
            return null;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreUsuario(nombreUsuario);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasena(contrasenaEnTextoPlano);
        nuevoUsuario.setPaisIso(paisIso);

        Usuario usuarioInsertado = usuarioDAO.insertar(nuevoUsuario);

        // --- SECCIÓN CRÍTICA ---
        // Asegúrate de que este bloque "if" exista y no esté comentado.
        // Es el responsable de crear la configuración.
        if (usuarioInsertado != null) {
            logger.info("Usuario insertado. Llamando al servicio para asignar configuración inicial al usuario ID: {}", usuarioInsertado.getId());
            configService.asignarConfiguracionInicial(usuarioInsertado);
        }
        // --- FIN DE LA SECCIÓN CRÍTICA ---

        return usuarioInsertado;
    }

    public boolean verificarLogin(String email, String contrasenaEnTextoPlano) {
        Optional<Usuario> usuarioOpt = usuarioDAO.consultarPorEmail(email);

        return usuarioOpt.map(usuario ->
                BCrypt.checkpw(contrasenaEnTextoPlano, usuario.getContrasena())
        ).orElse(false);
    }

    public void eliminarUsuario(int id) {
        usuarioDAO.eliminar(id);
        logger.info("Solicitud para eliminar usuario con ID: {}", id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.consultarPorEmail(email);
    }
}