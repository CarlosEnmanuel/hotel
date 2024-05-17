package com.example.hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Horario {
    private LocalDate fechaEntrada;
    private LocalTime horaEntrada;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;

    public Horario(LocalDate fechaEntrada, LocalTime horaEntrada, LocalDate fechaSalida, LocalTime horaSalida) {
        this.fechaEntrada = fechaEntrada;
        this.horaEntrada = horaEntrada;
        this.fechaSalida = fechaSalida;
        this.horaSalida = horaSalida;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "fechaEntrada=" + fechaEntrada +
                ", horaEntrada=" + horaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", horaSalida=" + horaSalida +
                '}';
    }

    public void saveToDatabase() throws SQLException {
        String query = "INSERT INTO horarios (fechaEntrada, horaEntrada, fechaSalida, horaSalida) VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, java.sql.Date.valueOf(fechaEntrada));
            statement.setString(2, horaEntrada.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            statement.setDate(3, java.sql.Date.valueOf(fechaSalida));
            statement.setString(4, horaSalida.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            statement.executeUpdate();
        }
    }

    public static List<Horario> obtenerTodosLosHorarios() throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        String query = "SELECT * FROM horarios";

        try (Connection connection = Conexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {

                LocalDate fechaEntrada = resultSet.getDate("fechaEntrada").toLocalDate();
                LocalTime horaEntrada = LocalTime.parse(resultSet.getString("horaEntrada"));
                LocalDate fechaSalida = resultSet.getDate("fechaSalida").toLocalDate();
                LocalTime horaSalida = LocalTime.parse(resultSet.getString("horaSalida"));

                Horario horario = new Horario(fechaEntrada, horaEntrada, fechaSalida, horaSalida);
                horarios.add(horario);
            }
        }

        return horarios;
    }


}
