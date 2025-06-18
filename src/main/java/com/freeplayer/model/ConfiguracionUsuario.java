package com.freeplayer.model;

public class ConfiguracionUsuario {
    private int id;
    private Usuario usuario; // Guardamos el objeto Usuario completo
    private TemaUI tema;     // Guardamos el objeto TemaUI completo

    public ConfiguracionUsuario() {
    }

    // Getters, Setters y toString()...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TemaUI getTema() { return tema; }
    public void setTema(TemaUI tema) { this.tema = tema; }

    @Override
    public String toString() {
        return "ConfiguraciónUsuario{" + "id=" + id + ", usuario=" + usuario.getNombreUsuario() + ", tema=" + tema.getNombreTema() + '}';
    }
}