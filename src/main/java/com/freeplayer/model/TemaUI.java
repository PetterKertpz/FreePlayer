package com.freeplayer.model;

public class TemaUI {
    private int id;
    private String nombreTema;
    private String configuracionJson; // Guardamos el JSON como un String
    private Integer idPropietario; // Usamos Integer para que pueda ser null

    // Constructor, Getters, Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreTema() { return nombreTema; }
    public void setNombreTema(String nombreTema) { this.nombreTema = nombreTema; }
    public String getConfiguracionJson() { return configuracionJson; }
    public void setConfiguracionJson(String configuracionJson) { this.configuracionJson = configuracionJson; }
    public Integer getIdPropietario() { return idPropietario; }
    public void setIdPropietario(Integer idPropietario) { this.idPropietario = idPropietario; }

    @Override
    public String toString() {
        return "TemaUI{" + "id=" + id + ", nombreTema='" + nombreTema + '\'' + '}';
    }
}