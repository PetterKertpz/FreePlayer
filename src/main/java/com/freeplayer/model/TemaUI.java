package com.freeplayer.model;

public class TemaUI {
    private int id;
    private String nombreTema;
    private String configuracionJson; // Guardamos el JSON como un String

    // Constructor sin parámetros
    public TemaUI() {
    }

    // Getters, Setters y toString()...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreTema() { return nombreTema; }
    public void setNombreTema(String nombreTema) { this.nombreTema = nombreTema; }
    public String getConfiguracionJson() { return configuracionJson; }
    public void setConfiguracionJson(String configuracionJson) { this.configuracionJson = configuracionJson; }

    @Override
    public String toString() {
        return "TemaUI{" + "id=" + id + ", nombreTema='" + nombreTema + '\'' + '}';
    }
}