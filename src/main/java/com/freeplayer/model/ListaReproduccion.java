package com.freeplayer.model;
import java.time.LocalDateTime;


public class ListaReproduccion {
    private int idlista;
    private int idUsuario;
    private String nombreLista;
    private LocalDateTime fechaCreacion;

    public ListaReproduccion() {
    }

    public ListaReproduccion(int idlista, int idUsuario, String nombreLista, LocalDateTime fechaCreacion) {
        this.idlista = idlista;
        this.idUsuario = idUsuario;
        this.nombreLista = nombreLista;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdlista() {
        return idlista;
    }

    public void setIdlista(int idlista) {
        this.idlista = idlista;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreLista() {
        return nombreLista;
    }

    public void setNombreLista(String nombreLista) {
        this.nombreLista = nombreLista;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "ListaReproduccion{" +
                "idlista=" + idlista +
                ", idUsuario=" + idUsuario +
                ", nombreLista='" + nombreLista + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
