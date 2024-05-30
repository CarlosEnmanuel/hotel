package com.example.hotel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ReservacionesController {

    @FXML
    private ComboBox<String> clienteComboBox;

    @FXML
    private ComboBox<String> habitacionComboBox;

    @FXML
    private ComboBox<String> servicioComboBox;

    @FXML
    private ComboBox<String> horarioComboBox;

    @FXML
    private ComboBox<String> frecuenciaComboBox;

    @FXML
    private TableView<Reservacion> reservacionesTableView;

    @FXML
    private TableColumn<Reservacion, Integer> coodigoReservacionColumn;

    @FXML
    private TableColumn<Reservacion, String> nombreClienteColumn;

    @FXML
    private TableColumn<Reservacion, String> tipoHabitacionColumn;

    @FXML
    private TableColumn<Reservacion, String> tipoServicioColumn;

    @FXML
    private TableColumn<Reservacion, String> horarioColumn;

    @FXML
    private TableColumn<Reservacion, String> frecuenciasColumn;

    @FXML
    private TableColumn<Reservacion, Double> totalColumn;

    @FXML
    private Button guardarButton;

    @FXML
    private Button limpiarButton;

    @FXML
    private Button eliminarButton;

    @FXML
    private Button actualizarButton;

    private ObservableList<String> clientes = FXCollections.observableArrayList();
    private ObservableList<String> habitaciones = FXCollections.observableArrayList();
    private ObservableList<String> servicios = FXCollections.observableArrayList();
    private ObservableList<String> horarios = FXCollections.observableArrayList();
    private ObservableList<String> frecuencias = FXCollections.observableArrayList();
    private ObservableList<Reservacion> reservaciones = FXCollections.observableArrayList();

    public void initialize() {

        coodigoReservacionColumn.setCellValueFactory(new PropertyValueFactory<>("coodigoReservacion"));
        nombreClienteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerNombreCliente(cellData.getValue().getCodigoUsuario())));
        tipoHabitacionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerTipoHabitacion(cellData.getValue().getCodigoHabitacion())));
        tipoServicioColumn.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerTipoServicio(cellData.getValue().getCodigoServicio())));
        horarioColumn.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerHorario(cellData.getValue().getCodigoHorario())));
        frecuenciasColumn.setCellValueFactory(new PropertyValueFactory<>("frecuencias"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        reservacionesTableView.setItems(reservaciones);

        loadData();
    }

    private void loadData() {
        loadClientes();
        loadHabitaciones();
        loadServicios();
        loadHorarios();
        loadFrecuencias();
        loadReservaciones();
    }

    private void loadClientes() {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT nombreUsuario FROM cliente";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                clientes.add(resultSet.getString("nombreUsuario"));
            }
            clienteComboBox.setItems(clientes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHabitaciones() {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT tipoDeHabitacion FROM habitaciones";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                habitaciones.add(resultSet.getString("tipoDeHabitacion"));
            }
            habitacionComboBox.setItems(habitaciones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadServicios() {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT tipoServicio FROM servicios";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                servicios.add(resultSet.getString("tipoServicio"));
            }
            servicioComboBox.setItems(servicios);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHorarios() {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT fechaEntrada, fechaSalida FROM horarios";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String horario = resultSet.getString("fechaEntrada") + " - " + resultSet.getString("fechaSalida");
                horarios.add(horario);
            }
            horarioComboBox.setItems(horarios);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFrecuencias() {
        frecuencias.addAll("Diaria", "Semanal", "Mensual");
        frecuenciaComboBox.setItems(frecuencias);
    }

    private void loadReservaciones() {
        reservaciones.clear();
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT * FROM reservaciones";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Reservacion reservacion = new Reservacion(
                        resultSet.getInt("coodigoReservacion"),
                        resultSet.getInt("codigoHabitacion"),
                        resultSet.getInt("codigoHorario"),
                        resultSet.getInt("codigoServicio"),
                        resultSet.getInt("codigoUsuario"),
                        resultSet.getString("frecuencias"),
                        resultSet.getDouble("total")
                );
                reservaciones.add(reservacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void guardarReservacion() {
        String cliente = clienteComboBox.getValue();
        String habitacion = habitacionComboBox.getValue();
        String servicio = servicioComboBox.getValue();
        String horario = horarioComboBox.getValue();
        String frecuencia = frecuenciaComboBox.getValue();

        if (cliente == null || habitacion == null || servicio == null || horario == null || frecuencia == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try (Connection connection = Conexion.getConnection()) {
            String sql = "INSERT INTO reservaciones (codigoHabitacion, codigoHorario, codigoServicio, codigoUsuario, frecuencias, total) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, obtenerCodigoHabitacion(habitacion));
            statement.setInt(2, obtenerCodigoHorario(horario));
            statement.setInt(3, obtenerCodigoServicio(servicio));
            statement.setInt(4, obtenerCodigoCliente(cliente));
            statement.setString(5, frecuencia);
            statement.setDouble(6, calcularTotal());

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                mostrarAlerta("Éxito", "Reservación guardada correctamente.");
                loadReservaciones();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la reservación.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int obtenerCodigoCliente(String nombreUsuario) {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT codigoUsuario FROM cliente WHERE nombreUsuario = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nombreUsuario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("codigoUsuario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int obtenerCodigoHabitacion(String tipoDeHabitacion) {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT codigoHabitacion FROM habitaciones WHERE tipoDeHabitacion = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tipoDeHabitacion);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("codigoHabitacion");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int obtenerCodigoServicio(String tipoServicio) {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT codigoServicio FROM servicios WHERE tipoServicio = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tipoServicio);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("codigoServicio");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int obtenerCodigoHorario(String horario) {
        try (Connection connection = Conexion.getConnection()) {
            String[] partes = horario.split(" - ");
            String fechaEntrada = partes[0];
            String fechaSalida = partes[1];
            String query = "SELECT codigoHorario FROM horarios WHERE fechaEntrada = ? AND fechaSalida = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, fechaEntrada);
            statement.setString(2, fechaSalida);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("codigoHorario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String obtenerNombreCliente(int codigoUsuario) {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT nombreUsuario FROM cliente WHERE codigoUsuario = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigoUsuario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nombreUsuario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String obtenerTipoHabitacion(int codigoHabitacion) {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT tipoDeHabitacion FROM habitaciones WHERE codigoHabitacion = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigoHabitacion);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("tipoDeHabitacion");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String obtenerTipoServicio(int codigoServicio) {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT tipoServicio FROM servicios WHERE codigoServicio = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigoServicio);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("tipoServicio");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String obtenerHorario(int codigoHorario) {
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT fechaEntrada, fechaSalida FROM horarios WHERE codigoHorario = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigoHorario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("fechaEntrada") + " - " + resultSet.getString("fechaSalida");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double calcularTotal() {
        String horario = horarioComboBox.getValue();
        String[] partes = horario.split(" - ");
        String fechaEntrada = partes[0].trim();
        String fechaSalida = partes[1].trim();

        double precioPorNoche = obtenerCostoHabitacion(obtenerCodigoHabitacion(habitacionComboBox.getValue()));
        double costoServicio = obtenerCostoServicio(obtenerCodigoServicio(servicioComboBox.getValue()));
        long noches = calcularNoches(fechaEntrada, fechaSalida);

        return (precioPorNoche * noches) + (costoServicio * noches);
    }

    private long calcularNoches(String fechaEntrada, String fechaSalida) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate entrada = LocalDate.parse(fechaEntrada, formatter);
            LocalDate salida = LocalDate.parse(fechaSalida, formatter);
            return ChronoUnit.DAYS.between(entrada, salida);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double obtenerCostoHabitacion(int codigoHabitacion) {
        double precioPorNoche = 0;
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT precio FROM habitaciones WHERE codigoHabitacion = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigoHabitacion);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                precioPorNoche = resultSet.getDouble("precio");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return precioPorNoche;
    }

    private double obtenerCostoServicio(int codigoServicio) {
        double costoServicio = 0;
        try (Connection connection = Conexion.getConnection()) {
            String query = "SELECT costo FROM servicios WHERE codigoServicio = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigoServicio);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                costoServicio = resultSet.getDouble("costo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return costoServicio;
    }

    @FXML
    void limpiarCampos() {
        clienteComboBox.setValue(null);
        habitacionComboBox.setValue(null);
        servicioComboBox.setValue(null);
        horarioComboBox.setValue(null);
        frecuenciaComboBox.setValue(null);
    }

    @FXML
    void eliminarReservacion() {
        Reservacion reservacionSeleccionada = reservacionesTableView.getSelectionModel().getSelectedItem();

        if (reservacionSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, selecciona una reservación para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar esta reservación?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = Conexion.getConnection()) {
                String query = "DELETE FROM reservaciones WHERE coodigoReservacion = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, reservacionSeleccionada.getCoodigoReservacion());

                int filasAfectadas = statement.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarAlerta("Éxito", "Reservación eliminada correctamente.");
                    loadReservaciones();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la reservación.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void actualizarReservacion() {
        Reservacion reservacionSeleccionada = reservacionesTableView.getSelectionModel().getSelectedItem();

        if (reservacionSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, selecciona una reservación para actualizar.");
            return;
        }

        String cliente = clienteComboBox.getValue();
        String habitacion = habitacionComboBox.getValue();
        String servicio = servicioComboBox.getValue();
        String horario = horarioComboBox.getValue();
        String frecuencia = frecuenciaComboBox.getValue();

        if (cliente == null || habitacion == null || servicio == null || horario == null || frecuencia == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try (Connection connection = Conexion.getConnection()) {
            String sql = "UPDATE reservaciones SET codigoHabitacion = ?, codigoHorario = ?, codigoServicio = ?, codigoUsuario = ?, frecuencias = ?, total = ? WHERE coodigoReservacion = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, obtenerCodigoHabitacion(habitacion));
            statement.setInt(2, obtenerCodigoHorario(horario));
            statement.setInt(3, obtenerCodigoServicio(servicio));
            statement.setInt(4, obtenerCodigoCliente(cliente));
            statement.setString(5, frecuencia);
            statement.setDouble(6, calcularTotal());
            statement.setInt(7, reservacionSeleccionada.getCoodigoReservacion());

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                mostrarAlerta("Éxito", "Reservación actualizada correctamente.");
                loadReservaciones();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la reservación.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irAInicio(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Inicio.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irACliente(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Cliente.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAReservaciones(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservaciones.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAEmpleados(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Empleados.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAHorarios(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Horario.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAHabitaciones(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Habitaciones.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAServicios(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Servicios.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAProveedores(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Proveedores.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void irAReportes(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reportes.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void userLogOut(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de salida");
        alert.setHeaderText("¿Está seguro de que desea salir del sitio?");
        alert.setContentText("Seleccione Aceptar para salir o Cancelar para continuar.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
