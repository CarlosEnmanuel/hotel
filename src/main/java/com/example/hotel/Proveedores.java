package com.example.hotel;

public class Proveedores {
    private int codigoProveedor;
    private String nombreEmpresa;
    private String telefono;
    private String direccion;
    private String nombreContacto;
    private int codigoSucursal;

    public Proveedores(int codigoProveedor, String nombreEmpresa, String telefono, String direccion, String nombreContacto) {
        this.codigoProveedor = codigoProveedor;
        this.nombreEmpresa = nombreEmpresa;
        this.telefono = telefono;
        this.direccion = direccion;
        this.nombreContacto = nombreContacto;
        this.codigoSucursal = 1; // Siempre establecer a 1
    }

    // Getters y setters

    public int getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(int codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public int getCodigoSucursal() {
        return codigoSucursal;
    }

    public void setCodigoSucursal(int codigoSucursal) {
        this.codigoSucursal = codigoSucursal;
    }
}

