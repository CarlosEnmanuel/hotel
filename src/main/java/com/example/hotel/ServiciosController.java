package com.example.hotel;



import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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

public class ServiciosController {

    @FXML
    private TextField tipoServicioField;

    @FXML
    private TextField costoField;

    @FXML
    private TableView<Servicio> tablaServicios;

    @FXML
    private TableColumn<Servicio, Integer> codigoServicioColumn;

    @FXML
    private TableColumn<Servicio, String> tipoServicioColumn;

    @FXML
    private TableColumn<Servicio, String> costoColumn;

    private ObservableList<Servicio> servicios = FXCollections.observableArrayList();

    public void initialize() {
        codigoServicioColumn.setCellValueFactory(new PropertyValueFactory<>("codigoServicio"));
        tipoServicioColumn.setCellValueFactory(new PropertyValueFactory<>("tipoServicio"));
        costoColumn.setCellValueFactory(new PropertyValueFactory<>("costo"));

        tablaServicios.setItems(servicios);

        // Validación en tiempo real para tipoServicioField
        tipoServicioField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32) {
                tipoServicioField.setText(oldValue);
            } else if (!newValue.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
                tipoServicioField.setText(oldValue);
            }
        });

        // Validación en tiempo real para costoField
        costoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 8) {
                costoField.setText(oldValue);
            } else if (!newValue.matches("\\d*")) {
                costoField.setText(oldValue);
            }
        });

        cargarServiciosDesdeBaseDeDatos();
    }

    private void cargarServiciosDesdeBaseDeDatos() {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "SELECT * FROM servicios";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            servicios.clear();

            while (rs.next()) {
                int codigo = rs.getInt("codigoServicio");
                String tipo = rs.getString("tipoServicio");
                String costo = rs.getString("costo");

                Servicio servicio = new Servicio(codigo, tipo, costo);
                servicios.add(servicio);
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
    void guardarServicio() {
        String tipoServicio = tipoServicioField.getText();
        String costo = costoField.getText();

        if (tipoServicio.isEmpty() || costo.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validación de formato
        if (tipoServicio.length() > 32 || !tipoServicio.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
            mostrarAlerta("Error", "El campo de tipo de servicio debe contener solo letras y un máximo de 32 caracteres.");
            return;
        }

        if (costo.length() > 8 || !costo.matches("\\d*")) {
            mostrarAlerta("Error", "El campo de costo debe contener solo números y un máximo de 8 caracteres.");
            return;
        }

        Servicio servicio = new Servicio(0, tipoServicio, costo);
        if (guardarServicioEnBD(servicio)) {
            mostrarAlerta("Servicio Guardado", "El servicio se ha guardado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo guardar el servicio en la base de datos.");
        }

        limpiarCampos();
        cargarServiciosDesdeBaseDeDatos();
    }

    private boolean guardarServicioEnBD(Servicio servicio) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "INSERT INTO servicios (tipoServicio, costo) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, servicio.getTipoServicio());
            pstmt.setString(2, servicio.getCosto());

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            return false;
        }
    }

    private boolean actualizarServicioEnBD(Servicio servicio) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "UPDATE servicios SET tipoServicio = ?, costo = ? WHERE codigoServicio = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, servicio.getTipoServicio());
            pstmt.setString(2, servicio.getCosto());
            pstmt.setInt(3, servicio.getCodigoServicio());

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
    void actualizarServicio() {
        Servicio servicioSeleccionado = tablaServicios.getSelectionModel().getSelectedItem();

        if (servicioSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un servicio para actualizar.");
            return;
        }

        String tipoServicio = tipoServicioField.getText();
        String costo = costoField.getText();

        if (tipoServicio.isEmpty() || costo.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        servicioSeleccionado.setTipoServicio(tipoServicio);
        servicioSeleccionado.setCosto(costo);

        if (actualizarServicioEnBD(servicioSeleccionado)) {
            mostrarAlerta("Servicio Actualizado", "El servicio se ha actualizado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el servicio en la base de datos.");
        }

        limpiarCampos();
        cargarServiciosDesdeBaseDeDatos();
    }

    @FXML
    void eliminarServicio() {
        Servicio servicioSeleccionado = tablaServicios.getSelectionModel().getSelectedItem();

        if (servicioSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un servicio para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este servicio?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection conn = Conexion.getConnection();
                String sql = "DELETE FROM servicios WHERE codigoServicio = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, servicioSeleccionado.getCodigoServicio());

                int filasAfectadas = pstmt.executeUpdate();

                pstmt.close();
                conn.close();

                if (filasAfectadas > 0) {
                    servicios.remove(servicioSeleccionado);
                    mostrarAlerta("Servicio Eliminado", "El servicio se ha eliminado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el servicio.");
                }
            } catch (SQLException e) {
                mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            }
        }
    }

    @FXML
    void limpiarCampos() {
        tipoServicioField.clear();
        costoField.clear();
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
        Alert alert = new Alert(AlertType.CONFIRMATION);
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
