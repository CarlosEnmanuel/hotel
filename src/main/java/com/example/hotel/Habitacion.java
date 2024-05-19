package com.example.hotel;

public class Habitacion {
    private int codigoHabitacion;
    private String tipoDeHabitacion;
    private String descripcion;
    private String precio;

    public Habitacion() {}

    public Habitacion(int codigoHabitacion, String tipoDeHabitacion, String descripcion, String precio) {
        this.codigoHabitacion = codigoHabitacion;
        this.tipoDeHabitacion = tipoDeHabitacion;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    // Getters y setters
    public int getCodigoHabitacion() {
        return codigoHabitacion;
    }

    public void setCodigoHabitacion(int codigoHabitacion) {
        this.codigoHabitacion = codigoHabitacion;
    }

    public String getTipoDeHabitacion() {
        return tipoDeHabitacion;
    }

    public void setTipoDeHabitacion(String tipoDeHabitacion) {
        this.tipoDeHabitacion = tipoDeHabitacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
