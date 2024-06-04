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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class ProveedoresController {

    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField telefonoTextField;
    @FXML
    private TextField direccionTextField;
    @FXML
    private TextField representanteTextField;
    @FXML
    private TableView<Proveedores> proveedoresTableView;
    @FXML
    private TableColumn<Proveedores, Integer> codigoProveedoresColumn;
    @FXML
    private TableColumn<Proveedores, String> nombreColumn;
    @FXML
    private TableColumn<Proveedores, String> telefonoColumn;
    @FXML
    private TableColumn<Proveedores, String> direccionColumn;
    @FXML
    private TableColumn<Proveedores, String> nombreContactoColumn;

    @FXML
    private Button guardarButton;
    @FXML
    private Button limpiarButton;
    @FXML
    private Button eliminarButton;
    @FXML
    private Button actualizarButton;


    private ObservableList<Proveedores> proveedores = FXCollections.observableArrayList();
    private boolean isUpdatingTelefono = false;

    public void initialize() {
        codigoProveedoresColumn.setCellValueFactory(new PropertyValueFactory<>("codigoProveedor"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreEmpresa"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        nombreContactoColumn.setCellValueFactory(new PropertyValueFactory<>("nombreContacto"));

        proveedoresTableView.setItems(proveedores);

        // Validación en tiempo real para nombreTextField
        nombreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                nombreTextField.setText(oldValue);
            } else if (!newValue.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
                nombreTextField.setText(oldValue);
            }
        });

        // Validación en tiempo real para direccionTextField
        direccionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32) {
                direccionTextField.setText(oldValue);
            } else if (!newValue.matches("[\\p{L}0-9áéíóúÁÉÍÓÚñÑ\\s#]*")) {
                direccionTextField.setText(oldValue);
            }
        });

        // Validación en tiempo real para telefonoTextField
        telefonoTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdatingTelefono) {
                isUpdatingTelefono = true;
                Platform.runLater(() -> {
                    if (newValue.length() > 9) {
                        telefonoTextField.setText(oldValue);
                    } else {
                        telefonoTextField.setText(formatPhoneNumber(newValue));
                    }
                    telefonoTextField.positionCaret(telefonoTextField.getText().length());
                    isUpdatingTelefono = false;
                });
            }
        });

        // Validación en tiempo real para representanteTextField
        representanteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                representanteTextField.setText(oldValue);
            } else if (!newValue.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
                representanteTextField.setText(oldValue);
            }
        });

        cargarProveedoresDesdeBaseDeDatos();
    }

    private String formatPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("[^0-9]", ""); // Eliminar caracteres no numéricos
        if (phoneNumber.length() > 4) {
            phoneNumber = phoneNumber.substring(0, 4) + '-' + phoneNumber.substring(4);
        }
        return phoneNumber;
    }

    private void cargarProveedoresDesdeBaseDeDatos() {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "SELECT * FROM proveedores";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            proveedores.clear();

            while (rs.next()) {
                int codigoProveedor = rs.getInt("codigoProveedor");
                String nombreEmpresa = rs.getString("nombreEmpresa");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");
                String nombreContacto = rs.getString("nombreContacto");

                Proveedores proveedor = new Proveedores(codigoProveedor, nombreEmpresa, telefono, direccion, nombreContacto);
                proveedores.add(proveedor);
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
    void guardarProveedor() {
        String nombreEmpresa = nombreTextField.getText();
        String telefono = telefonoTextField.getText();
        String direccion = direccionTextField.getText();
        String nombreContacto = representanteTextField.getText();

        if (nombreEmpresa.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || nombreContacto.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validación de formato
        if (nombreEmpresa.length() > 10 || !nombreEmpresa.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
            mostrarAlerta("Error", "El campo de nombre debe contener solo letras y un máximo de 10 caracteres.");
            return;
        }

        if (direccion.length() > 32 || !direccion.matches("[\\p{L}0-9áéíóúÁÉÍÓÚñÑ\\s#]*")) {
            mostrarAlerta("Error", "El campo de dirección debe contener solo letras, números y el carácter '#' con un máximo de 32 caracteres.");
            return;
        }

        if (telefono.length() > 8 || !telefono.matches("\\d*")) {
            mostrarAlerta("Error", "El campo de teléfono debe contener solo números y un máximo de 8 caracteres.");
            return;
        }

        if (nombreContacto.length() > 50 || !nombreContacto.matches("[\\p{L}áéíóúÁÉÍÓÚñÑ\\s]*")) {
            mostrarAlerta("Error", "El campo de representante debe contener solo letras y un máximo de 50 caracteres.");
            return;
        }

        Proveedores proveedor = new Proveedores(0, nombreEmpresa, telefono, direccion, nombreContacto);
        if (guardarProveedorEnBD(proveedor)) {
            mostrarAlerta("Proveedor Guardado", "El proveedor se ha guardado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo guardar el proveedor en la base de datos.");
        }

        limpiarCampos();
        cargarProveedoresDesdeBaseDeDatos();
    }

    private boolean guardarProveedorEnBD(Proveedores proveedores) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "INSERT INTO proveedores (nombreEmpresa, telefono, direccion, nombreContacto, codigoSucursal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, proveedores.getNombreEmpresa());
            pstmt.setString(2, proveedores.getTelefono());
            pstmt.setString(3, proveedores.getDireccion());
            pstmt.setString(4, proveedores.getNombreContacto());
            pstmt.setInt(5, 1); // Establecer el valor de codigoSucursal a 1

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
    void eliminarProveedor() {
        Proveedores proveedorSeleccionado = proveedoresTableView.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un proveedor para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este proveedor?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection conn = Conexion.getConnection();
                String sql = "DELETE FROM proveedores WHERE codigoProveedor = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, proveedorSeleccionado.getCodigoProveedor());

                int filasAfectadas = pstmt.executeUpdate();

                pstmt.close();
                conn.close();

                if (filasAfectadas > 0) {
                    proveedores.remove(proveedorSeleccionado);
                    mostrarAlerta("Proveedor Eliminado", "El proveedor se ha eliminado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el proveedor.");
                }
            } catch (SQLException e) {
                mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            }
        }
    }

    @FXML
    void actualizarProveedor() {
        Proveedores proveedorSeleccionado = proveedoresTableView.getSelectionModel().getSelectedItem();

        if (proveedorSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un proveedor para actualizar.");
            return;
        }

        String nombreEmpresa = nombreTextField.getText();
        String telefono = telefonoTextField.getText();
        String direccion = direccionTextField.getText();
        String nombreContacto = representanteTextField.getText();

        if (nombreEmpresa.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || nombreContacto.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        Proveedores proveedorActualizado = new Proveedores(proveedorSeleccionado.getCodigoProveedor(), nombreEmpresa, telefono, direccion, nombreContacto);

        try {
            Connection conn = Conexion.getConnection();
            String sql = "UPDATE proveedores SET nombreEmpresa = ?, telefono = ?, direccion = ?, nombreContacto = ? WHERE codigoProveedor = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, proveedorActualizado.getNombreEmpresa());
            pstmt.setString(2, proveedorActualizado.getTelefono());
            pstmt.setString(3, proveedorActualizado.getDireccion());
            pstmt.setString(4, proveedorActualizado.getNombreContacto());
            pstmt.setInt(5, proveedorActualizado.getCodigoProveedor());

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if (filasAfectadas > 0) {
                int indiceProveedor = proveedores.indexOf(proveedorSeleccionado);
                proveedores.set(indiceProveedor, proveedorActualizado);
                mostrarAlerta("Proveedor Actualizado", "El proveedor se ha actualizado correctamente.");
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el proveedor.");
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
        }

        limpiarCampos();
    }

    @FXML
    void limpiarCampos() {
        nombreTextField.clear();
        telefonoTextField.clear();
        direccionTextField.clear();
        representanteTextField.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
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
