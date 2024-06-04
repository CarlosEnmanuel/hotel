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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class HabitacionesController {

    @FXML
    private TextField codigoField;
    @FXML
    private TextField tipoField;
    @FXML
    private TextField descripcionField;
    @FXML
    private TextField precioField;
    @FXML
    private TableView<Habitacion> tablaHabitaciones;
    @FXML
    private TableColumn<Habitacion, Integer> codigoColumn;
    @FXML
    private TableColumn<Habitacion, String> tipoColumn;
    @FXML
    private TableColumn<Habitacion, String> descripcionColumn;
    @FXML
    private TableColumn<Habitacion, String> precioColumn;

    private ObservableList<Habitacion> habitaciones = FXCollections.observableArrayList();

    public void initialize() {
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigoHabitacion"));
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipoDeHabitacion"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));

        tablaHabitaciones.setItems(habitaciones);

        // Validación en tiempo real para tipoField y descripcionField
        tipoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32) {
                tipoField.setText(oldValue);
            } else if (!newValue.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
                tipoField.setText(oldValue);
            }
        });

        descripcionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32) {
                descripcionField.setText(oldValue);
            } else if (!newValue.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
                descripcionField.setText(oldValue);
            }
        });

        // Validación en tiempo real para precioField
        precioField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 8) {
                precioField.setText(oldValue);
            } else if (!newValue.matches("\\d*")) {
                precioField.setText(oldValue);
            }
        });

        cargarHabitacionesDesdeBaseDeDatos();
    }

    private void cargarHabitacionesDesdeBaseDeDatos() {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "SELECT * FROM habitaciones";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            habitaciones.clear();

            while (rs.next()) {
                int codigo = rs.getInt("codigoHabitacion");
                String tipo = rs.getString("tipoDeHabitacion");
                String descripcion = rs.getString("descripcion");
                String precio = rs.getString("precio");

                Habitacion habitacion = new Habitacion(codigo, tipo, descripcion, precio);
                habitaciones.add(habitacion);
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
    void guardarHabitacion() {
        String tipo = tipoField.getText();
        String descripcion = descripcionField.getText();
        String precio = precioField.getText();

        if (tipo.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validación de formato
        if (tipo.length() > 32 || !tipo.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
            mostrarAlerta("Error", "El campo de tipo de habitación debe contener solo letras y un máximo de 32 caracteres.");
            return;
        }

        if (descripcion.length() > 32 || !descripcion.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
            mostrarAlerta("Error", "El campo de descripción debe contener solo letras y un máximo de 32 caracteres.");
            return;
        }

        if (precio.length() > 8 || !precio.matches("\\d*")) {
            mostrarAlerta("Error", "El campo de precio debe contener solo números y un máximo de 8 dígitos.");
            return;
        }

        Habitacion habitacion = new Habitacion(0, tipo, descripcion, precio);
        if (guardarHabitacionEnBD(habitacion)) {
            mostrarAlerta("Habitación Guardada", "La habitación se ha guardado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo guardar la habitación en la base de datos.");
        }

        limpiarCampos();
        cargarHabitacionesDesdeBaseDeDatos();
    }



    private boolean guardarHabitacionEnBD(Habitacion habitacion) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "INSERT INTO habitaciones (codigoHabitacion, tipoDeHabitacion, descripcion, precio) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, habitacion.getCodigoHabitacion());
            pstmt.setString(2, habitacion.getTipoDeHabitacion());
            pstmt.setString(3, habitacion.getDescripcion());
            pstmt.setString(4, habitacion.getPrecio());

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            return false;
        }
    }
    private boolean actualizarHabitacionEnBD(Habitacion habitacion) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "UPDATE habitaciones SET tipoDeHabitacion = ?, descripcion = ?, precio = ? WHERE codigoHabitacion = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, habitacion.getTipoDeHabitacion());
            pstmt.setString(2, habitacion.getDescripcion());
            pstmt.setString(3, habitacion.getPrecio());
            pstmt.setInt(4, habitacion.getCodigoHabitacion());

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
    void actualizarHabitacion() {
        Habitacion habitacionSeleccionada = tablaHabitaciones.getSelectionModel().getSelectedItem();

        if (habitacionSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, selecciona una habitación para actualizar.");
            return;
        }

        String tipo = tipoField.getText();
        String descripcion = descripcionField.getText();
        String precio = precioField.getText();

        if (tipo.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        habitacionSeleccionada.setTipoDeHabitacion(tipo);
        habitacionSeleccionada.setDescripcion(descripcion);
        habitacionSeleccionada.setPrecio(precio);

        if (actualizarHabitacionEnBD(habitacionSeleccionada)) {
            mostrarAlerta("Habitación Actualizada", "La habitación se ha actualizado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la habitación en la base de datos.");
        }

        limpiarCampos();
        cargarHabitacionesDesdeBaseDeDatos();
    }

    @FXML
    void eliminarHabitacion() {
        Habitacion habitacionSeleccionada = tablaHabitaciones.getSelectionModel().getSelectedItem();

        if (habitacionSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, selecciona una habitación para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar esta habitación?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection conn = Conexion.getConnection();
                String sql = "DELETE FROM habitaciones WHERE codigoHabitacion = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, habitacionSeleccionada.getCodigoHabitacion());

                int filasAfectadas = pstmt.executeUpdate();

                pstmt.close();
                conn.close();

                if (filasAfectadas > 0) {
                    habitaciones.remove(habitacionSeleccionada);
                    mostrarAlerta("Habitación Eliminada", "La habitación se ha eliminado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la habitación.");
                }
            } catch (SQLException e) {
                mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            }
        }
    }

    @FXML
    void limpiarCampos() {

        tipoField.clear();
        descripcionField.clear();
        precioField.clear();
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
        // Cargar la vista de inicio desde inicio.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Inicio.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de inicio
        Inicio inicioController = loader.getController();

        // Crear una nueva escena con la vista de inicio
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);


        // Mostrar la nueva vista
        stage.show();
    }

    @FXML
    private void irACliente(ActionEvent event) throws IOException {
        // Cargar la vista de tratamientos desde tratamientos.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Cliente.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de tratamientos (si es necesario)
        ClienteController tratamientosController = loader.getController();

        // Crear una nueva escena con la vista de tratamientos
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
        stage.show();
    }





    @FXML
    private void irAReservaciones(ActionEvent event) throws IOException {
        // Cargar la vista de tratamientos desde tratamientos.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservaciones.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de tratamientos (si es necesario)
        ReservacionesController tratamientosController = loader.getController();

        // Crear una nueva escena con la vista de tratamientos
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
        stage.show();
    }
    @FXML
    private void irAEmpleados(ActionEvent event) throws IOException {
        // Cargar la vista de inventario desde inventario.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Empleados.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de inventario (si es necesario)
        EmpleadosController inventarioController = loader.getController();

        // Crear una nueva escena con la vista de inventario
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
        stage.show();
    }

    @FXML
    private void irAHorarios(ActionEvent event) throws IOException {
        // Cargar la vista de empleados desde empleados.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Horario.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de empleados (si es necesario)
        HorariosController empleadoController = loader.getController();

        // Crear una nueva escena con la vista de empleados
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
        stage.show();
    }
    @FXML
    private void irAHabitaciones(ActionEvent event) throws IOException {
        // Cargar la vista de proveedor desde proveedor.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Habitaciones.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de proveedor (si es necesario)
        HabitacionesController proveedorController = loader.getController();

        // Crear una nueva escena con la vista de proveedor
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
        stage.show();
    }
    @FXML
    private void irAServicios(ActionEvent event) throws IOException {
        // Cargar la vista de reportes desde reportes.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Servicios.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de reportes (si es necesario)
        ServiciosController reportesController = loader.getController();

        // Crear una nueva escena con la vista de reportes
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
        stage.show();
    }

    @FXML
    private void irAProveedores(ActionEvent event) throws IOException {
        // Cargar la vista de reportes desde reportes.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Proveedores.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de reportes (si es necesario)
        ProveedoresController reportesController = loader.getController();

        // Crear una nueva escena con la vista de reportes
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
        stage.show();
    }

    @FXML
    private void irAReportes(ActionEvent event) throws IOException {
        // Cargar la vista de reportes desde reportes.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reportes.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista de reportes (si es necesario)
        ReportesController reportesController = loader.getController();

        // Crear una nueva escena con la vista de reportes
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        // Mostrar la nueva vista
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
