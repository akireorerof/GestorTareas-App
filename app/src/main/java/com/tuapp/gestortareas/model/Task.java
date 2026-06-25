package com.tuapp.gestortareas.model;

public class Task {

    private String id;
    private String titulo;
    private String descripcion;
    private Integer estado;

    public Task() {
    }

    public Task(String titulo, String descripcion, Integer estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public void setCompletada(boolean completada) { if (completada) this.estado = 1; else this.estado = 0; }

    public boolean isCompletada() {
        return estado != null && estado == 1;
    }
}