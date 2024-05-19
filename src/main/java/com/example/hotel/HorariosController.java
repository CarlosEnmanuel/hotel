package com.example.hotel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HorariosController {

    @FXML
    private DatePicker checkInDatePicker;
    @FXML
    private DatePicker checkOutDatePicker;
    @FXML
    private Spinner<LocalTime> checkInTimeSpinner;
    @FXML
    private Spinner<LocalTime> checkOutTimeSpinner;
    @FXML
    private TableView<Horario> tablaHorarios;
    @FXML
    private TableColumn<Horario, Integer> codigoHorarioColumn;
    @FXML
    private TableColumn<Horario, LocalDate> fechaEntradaColumn;
    @FXML
    private TableColumn<Horario, LocalTime> horaEntradaColumn;
    @FXML
    private TableColumn<Horario, LocalDate> fechaSalidaColumn;
    @FXML
    private TableColumn<Horario, LocalTime> horaSalidaColumn;

    private ObservableList<Horario> horarios = FXCollections.observableArrayList();

    public void initialize() {
        codigoHorarioColumn.setCellValueFactory(new PropertyValueFactory<>("codigoHorario"));
        fechaEntradaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
        horaEntradaColumn.setCellValueFactory(new PropertyValueFactory<>("horaEntrada"));
        fechaSalidaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        horaSalidaColumn.setCellValueFactory(new PropertyValueFactory<>("horaSalida"));

        tablaHorarios.setItems(horarios);

        configureTimeSpinner(checkInTimeSpinner);
        configureTimeSpinner(checkOutTimeSpinner);

        cargarHorariosDesdeBaseDeDatos();
    }

    private void configureTimeSpinner(Spinner<LocalTime> timeSpinner) {
        timeSpinner.setValueFactory(new SpinnerValueFactory<LocalTime>() {
            {
                setConverter(new StringConverter<LocalTime>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                    @Override
                    public String toString(LocalTime time) {
                        return time != null ? formatter.format(time) : "";
                    }

                    @Override
                    public LocalTime fromString(String string) {
                        return string != null && !string.isEmpty() ? LocalTime.parse(string, formatter) : null;
                    }
                });
                setValue(LocalTime.now());
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 10));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 10));
            }
        });
    }

    private void cargarHorariosDesdeBaseDeDatos() {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "SELECT * FROM horarios";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            horarios.clear();

            while (rs.next()) {
                int codigoHorario = rs.getInt("codigoHorario");
                LocalDate fechaEntrada = rs.getDate("fechaEntrada").toLocalDate();
                LocalTime horaEntrada = LocalTime.parse(rs.getString("horaEntrada"));
                LocalDate fechaSalida = rs.getDate("fechaSalida").toLocalDate();
                LocalTime horaSalida = LocalTime.parse(rs.getString("horaSalida"));

                Horario horario = new Horario(codigoHorario, fechaEntrada, horaEntrada, fechaSalida, horaSalida);
                horarios.add(horario);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
        }
    }

    @FXML
    void guardarHorario() {
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();
        LocalTime checkInTime = checkInTimeSpinner.getValue();
        LocalTime checkOutTime = checkOutTimeSpinner.getValue();

        if (checkInDate == null || checkOutDate == null || checkInTime == null || checkOutTime == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        Horario horario = new Horario(0, checkInDate, checkInTime, checkOutDate, checkOutTime);
        if (guardarHorarioEnBD(horario)) {
            mostrarAlerta("Horario Guardado", "El horario se ha guardado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo guardar el horario en la base de datos.");
        }

        limpiarCampos();
        cargarHorariosDesdeBaseDeDatos();
    }

    private boolean guardarHorarioEnBD(Horario horario) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "INSERT INTO horarios (fechaEntrada, horaEntrada, fechaSalida, horaSalida) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(horario.getFechaEntrada()));
            pstmt.setString(2, horario.getHoraEntrada().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            pstmt.setDate(3, java.sql.Date.valueOf(horario.getFechaSalida()));
            pstmt.setString(4, horario.getHoraSalida().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            return false;
        }
    }

    @FXML
    void eliminarHorario() {
        Horario horarioSeleccionado = tablaHorarios.getSelectionModel().getSelectedItem();

        if (horarioSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un horario para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este horario?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection conn = Conexion.getConnection();
                String sql = "DELETE FROM horarios WHERE codigoHorario = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, horarioSeleccionado.getCodigoHorario());

                int filasAfectadas = pstmt.executeUpdate();

                pstmt.close();
                conn.close();

                if (filasAfectadas > 0) {
                    horarios.remove(horarioSeleccionado);
                    mostrarAlerta("Horario Eliminado", "El horario se ha eliminado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el horario.");
                }
            } catch (SQLException e) {
                mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            }
        }
    }

    @FXML
    void actualizarHorario() {
        Horario horarioSeleccionado = tablaHorarios.getSelectionModel().getSelectedItem();

        if (horarioSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un horario para actualizar.");
            return;
        }

        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();
        LocalTime checkInTime = checkInTimeSpinner.getValue();
        LocalTime checkOutTime = checkOutTimeSpinner.getValue();

        if (checkInDate == null || checkOutDate == null || checkInTime == null || checkOutTime == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        Horario horarioActualizado = new Horario(horarioSeleccionado.getCodigoHorario(), checkInDate, checkInTime, checkOutDate, checkOutTime);

        try {
            Connection conn = Conexion.getConnection();
            String sql = "UPDATE horarios SET fechaEntrada = ?, horaEntrada = ?, fechaSalida = ?, horaSalida = ? WHERE codigoHorario = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(horarioActualizado.getFechaEntrada()));
            pstmt.setString(2, horarioActualizado.getHoraEntrada().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            pstmt.setDate(3, java.sql.Date.valueOf(horarioActualizado.getFechaSalida()));
            pstmt.setString(4, horarioActualizado.getHoraSalida().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            pstmt.setInt(5, horarioActualizado.getCodigoHorario());

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if (filasAfectadas > 0) {
                mostrarAlerta("Horario Actualizado", "El horario se ha actualizado correctamente.");
                cargarHorariosDesdeBaseDeDatos();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el horario.");
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
        }
    }

    @FXML
    void limpiarCampos() {
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
        checkInTimeSpinner.getValueFactory().setValue(LocalTime.now());
        checkOutTimeSpinner.getValueFactory().setValue(LocalTime.now());
    }
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    private void irAInicio(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Inicio.fxml"));
        Parent root = loader.load();
        Inicio inicioController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irACliente(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Cliente.fxml"));
        Parent root = loader.load();
        ClienteController tratamientosController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAReservaciones(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservaciones.fxml"));
        Parent root = loader.load();
        ReservacionesController tratamientosController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAEmpleados(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Empleados.fxml"));
        Parent root = loader.load();
        EmpleadosController inventarioController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAHorarios(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Horario.fxml"));
        Parent root = loader.load();
        HorariosController empleadoController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAHabitaciones(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Habitaciones.fxml"));
        Parent root = loader.load();
        HabitacionesController proveedorController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAServicios(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Servicios.fxml"));
        Parent root = loader.load();
        ServiciosController reportesController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAProveedores(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Proveedores.fxml"));
        Parent root = loader.load();
        ProveedoresController reportesController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAReportes(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reportes.fxml"));
        Parent root = loader.load();
        ReportesController reportesController = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    public void userLogOut(ActionEvent event) {
        // Mostrar un cuadro de diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de salida");
        alert.setHeaderText("¿Está seguro de que desea salir del sitio?");
        alert.setContentText("Seleccione Aceptar para salir o Cancelar para continuar.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // El usuario seleccionó Aceptar, cerrar la aplicación
            Platform.exit();
        }
    }

}
