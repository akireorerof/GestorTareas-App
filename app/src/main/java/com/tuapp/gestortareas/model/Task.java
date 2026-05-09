package com.tuapp.gestortareas.model;

public class Task {
    private int id;
    private String titulo;
    private String descripcion;
    private int estado;

    public Task() {}

    public Task(int id, String titulo, String descripcion, int estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Task(String titulo, String descripcion, int estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public boolean isCompletada() { return estado == 1; }
}