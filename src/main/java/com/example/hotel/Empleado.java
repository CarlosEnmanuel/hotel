package com.example.hotel;

import java.time.LocalDate;

public class Empleado {

    private int codigoEmpleado;
    private String nombresEmpleado;
    private LocalDate fechaNacimiento;
    private String genero;
    private String dui;
    private String telefono;
    private String cargo;

    public Empleado(int codigoEmpleado, String nombresEmpleado, LocalDate fechaNacimiento, String genero, String dui, String telefono, String cargo) {
        this.codigoEmpleado = codigoEmpleado;
        this.nombresEmpleado = nombresEmpleado;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.dui = dui;
        this.telefono = telefono;
        this.cargo = cargo;
    }

    public int getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(int codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public String getNombreEmpleado() {
        return nombresEmpleado;
    }

    public void setNombreEmpleado(String nombresEmpleado) {
        this.nombresEmpleado = nombresEmpleado;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
