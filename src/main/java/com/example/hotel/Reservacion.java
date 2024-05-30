package com.example.hotel;

public class Reservacion {
    private int coodigoReservacion;
    private int codigoHabitacion;
    private int codigoHorario;
    private int codigoServicio;
    private int codigoUsuario;
    private String frecuencias;
    private double total;

    public Reservacion(int coodigoReservacion, int codigoHabitacion, int codigoHorario, int codigoServicio, int codigoUsuario, String frecuencias, double total) {
        this.coodigoReservacion = coodigoReservacion;
        this.codigoHabitacion = codigoHabitacion;
        this.codigoHorario = codigoHorario;
        this.codigoServicio = codigoServicio;
        this.codigoUsuario = codigoUsuario;
        this.frecuencias = frecuencias;
        this.total = total;
    }

    public int getCoodigoReservacion() {
        return coodigoReservacion;
    }

    public void setCoodigoReservacion(int coodigoReservacion) {
        this.coodigoReservacion = coodigoReservacion;
    }

    public int getCodigoHabitacion() {
        return codigoHabitacion;
    }

    public void setCodigoHabitacion(int codigoHabitacion) {
        this.codigoHabitacion = codigoHabitacion;
    }

    public int getCodigoHorario() {
        return codigoHorario;
    }

    public void setCodigoHorario(int codigoHorario) {
        this.codigoHorario = codigoHorario;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(int codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getFrecuencias() {
        return frecuencias;
    }

    public void setFrecuencias(String frecuencias) {
        this.frecuencias = frecuencias;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
