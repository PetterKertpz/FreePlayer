package com.freeplayer.model;

public class Autor {
    private int idAutor;
    private String nombreAutor;
    private String nacionalidadAutor;

    public Autor() {
    }
    public Autor(int idAutor, String nombreAutor, String nacionalidadAutor) {
        this.idAutor = idAutor;
        this.nombreAutor = nombreAutor;
        this.nacionalidadAutor = nacionalidadAutor;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getNacionalidadAutor() {
        return nacionalidadAutor;
    }

    public void setNacionalidadAutor(String nacionalidadAutor) {
        this.nacionalidadAutor = nacionalidadAutor;
    }

    @Override
    public String toString() {
        return "Autor{" +
                "idAutor=" + idAutor +
                ", nombreAutor='" + nombreAutor + '\'' +
                ", nacionalidadAutor='" + nacionalidadAutor + '\'' +
                '}';
    }
}
