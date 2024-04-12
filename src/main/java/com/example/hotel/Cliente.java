package com.example.hotel;

import java.time.LocalDate;

public class Cliente {
    private int codigoUsuario;
    private String nombreUsuario;
    private String genero;
    private LocalDate fechaNacimiento;
    private String dui;
    private String telefono;



    // Constructor
    public Cliente(int codigoUsuario, String nombreUsuario, String genero, LocalDate fechaNacimiento, String dui, String telefono) {
        this.codigoUsuario = codigoUsuario;
        this.nombreUsuario = nombreUsuario;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
        this.dui = dui;
        this.telefono = telefono;
    }

    // Getters y setters
    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(int codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
