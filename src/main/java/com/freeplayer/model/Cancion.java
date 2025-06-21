package com.freeplayer.model;

import java.time.Duration;
import java.time.LocalDateTime;



public class Cancion {
    private int           idCancion;
    private String        nombreCancion;
    private Duration      duracion;
    private int           idautor;
    private int           idgenero;
    private String        url;
    private int           idalbum;
    private String        urlMiniatura;
    private LocalDateTime fechaPublicacion;

    public Cancion() {
    }
    public Cancion(int id, String nombreCancion, int idautor, int idgenero, int idAlbum,Duration duracion,
                   String url,
                   String urlMiniatura, LocalDateTime fechaPublicacion) {
        this.idCancion = id;
        this.nombreCancion = nombreCancion;
        this.idautor = idautor;
        this.idgenero = idgenero;
        this.duracion = duracion;
        this.url = url;
        this.urlMiniatura = urlMiniatura;
        this.fechaPublicacion = fechaPublicacion;
    }
     public int getIdalbum() { // <--- 2. AÑADE ESTE METODO GETTER
        return idalbum;
    }

    public void setIdalbum(int idalbum) { // <--- 3. AÑADE ESTE METODO SETTER
        this.idalbum = idalbum;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
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


    public String getNombreCancion() {
        return nombreCancion;
    }

    public void setNombreCancion(String nombreCancion) {
        this.nombreCancion = nombreCancion;
    }

    public int getId() {
        return idCancion    ;
    }

    public void setId(int id) {
        this.idCancion = id;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "id=" + idCancion +
                ", nombreCancion='" + nombreCancion + '\'' +
                ", idautor=" + idautor +
                ", idgenero=" + idgenero +
                ", duracion=" + duracion +
                ", url='" + url + '\'' +
                ", urlMiniatura='" + urlMiniatura + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                '}';
    }
}
