package com.freeplayer.model;

import java.time.LocalDateTime;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String email;
    private String contrasena; // En esta clase, el campo guardará el HASH, no el texto plano
    private String paisIso;
    private LocalDateTime fechaRegistro;

    public Usuario() {
    }

    // Getters, Setters y toString()...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getPaisIso() { return paisIso; }
    public void setPaisIso(String paisIso) { this.paisIso = paisIso; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nombreUsuario='" + nombreUsuario + '\'' + ", email='" + email + '\'' + '}';
    }
}