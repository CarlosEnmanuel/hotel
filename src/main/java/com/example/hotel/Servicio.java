package com.example.hotel;

public class Servicio {
    private int codigoServicio;
    private String tipoServicio;
    private String costo;

    public Servicio(int codigoServicio, String tipoServicio, String costo) {
        this.codigoServicio = codigoServicio;
        this.tipoServicio = tipoServicio;
        this.costo = costo;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }
}
