package com.freeplayer.model;

import java.time.Duration;
import java.time.LocalDateTime;



public class Cancion {
    private int           id;
    private String        nombreCancion;
    private int           idautor;
    private int           idgenero;
    private int           idalbum;
    private Duration      duracion;
    private String        url;
    private String        urlMiniatura;
    private int           idListaReproduccion;
    private LocalDateTime fechaPublicacion;

    public Cancion() {
    }
    public Cancion(int id, String nombreCancion, int idautor, int idgenero, int idalbum, Duration duracion, String url, String urlMiniatura, int idListaReproduccion, LocalDateTime fechaPublicacion) {
        this.id = id;
        this.nombreCancion = nombreCancion;
        this.idautor = idautor;
        this.idgenero = idgenero;
        this.idalbum = idalbum;
        this.duracion = duracion;
        this.url = url;
        this.urlMiniatura = urlMiniatura;
        this.idListaReproduccion = idListaReproduccion;
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getIdListaReproduccion() {
        return idListaReproduccion;
    }

    public void setIdListaReproduccion(int idListaReproduccion) {
        this.idListaReproduccion = idListaReproduccion;
    }

    public String getUrlMiniatura() {
        return urlMiniatura;
    }

    public void setUrlMiniatura(String urlMiniatura) {
        this.urlMiniatura = urlMiniatura;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
    }

    public int getIdautor() {
        return idautor;
    }

    public void setIdautor(int idautor) {
        this.idautor = idautor;
    }

    public int getIdgenero() {
        return idgenero;
    }

    public void setIdgenero(int idgenero) {
        this.idgenero = idgenero;
    }

    public int getIdalbum() {
        return idalbum;
    }

    public void setIdalbum(int idalbum) {
        this.idalbum = idalbum;
    }

    public String getNombreCancion() {
        return nombreCancion;
    }

    public void setNombreCancion(String nombreCancion) {
        this.nombreCancion = nombreCancion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "id=" + id +
                ", nombreCancion='" + nombreCancion + '\'' +
                ", idautor=" + idautor +
                ", idgenero=" + idgenero +
                ", idalbum=" + idalbum +
                ", duracion=" + duracion +
                ", url='" + url + '\'' +
                ", urlMiniatura='" + urlMiniatura + '\'' +
                ", idListaReproduccion=" + idListaReproduccion +
                ", fechaPublicacion=" + fechaPublicacion +
                '}';
    }
}
