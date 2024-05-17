package com.example.hotel;

import javafx.application.Platform;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private TableColumn<Horario, Integer> idColumna;

    @FXML
    private TableColumn<Horario, LocalDate> fechaEntradaColumna;

    @FXML
    private TableColumn<Horario, LocalTime> horaEntradaColumna;

    @FXML
    private TableColumn<Horario, LocalDate> fechaSalidaColumna;

    @FXML
    private TableColumn<Horario, LocalTime> horaSalidaColumna;
    @FXML
    private void initialize() {
        configureTimeSpinner(checkInTimeSpinner);
        configureTimeSpinner(checkOutTimeSpinner);

        // Configurar las columnas de la tabla

        fechaEntradaColumna.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
        horaEntradaColumna.setCellValueFactory(new PropertyValueFactory<>("horaEntrada"));
        fechaSalidaColumna.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        horaSalidaColumna.setCellValueFactory(new PropertyValueFactory<>("horaSalida"));

        // Cargar los horarios cuando se inicializa la ventana
        cargarHorarios();
    }
    private void cargarHorarios() {
        try {
            // Obtener los horarios desde la base de datos
            List<Horario> horarios = Horario.obtenerTodosLosHorarios();

            // Limpiar la tabla
            tablaHorarios.getItems().clear();

            // Agregar los horarios a la tabla
            tablaHorarios.getItems().addAll(horarios);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los horarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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

    @FXML
    private void guardarHorarios() {
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();
        LocalTime checkInTime = checkInTimeSpinner.getValue();
        LocalTime checkOutTime = checkOutTimeSpinner.getValue();

        Horario horario = new Horario(checkInDate, checkInTime, checkOutDate, checkOutTime);

        try {
            horario.saveToDatabase();
            mostrarAlerta("Éxito", "Horario guardado exitosamente.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo guardar el horario: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void limpiarCampos() {
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
        checkInTimeSpinner.getValueFactory().setValue(LocalTime.now());
        checkOutTimeSpinner.getValueFactory().setValue(LocalTime.now());
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
