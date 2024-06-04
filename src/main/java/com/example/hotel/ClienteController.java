package com.example.hotel;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class ClienteController {

    @FXML
    private TextField nombreTextField;
    @FXML
    private ComboBox<String> generoComboBox;
    @FXML
    private TextField telefonoTextField;
    @FXML
    private TextField duiTextField;
    @FXML
    private DatePicker fechaNacimientoDatePicker;
    @FXML
    private TableView<Cliente> clientesTableView;
    @FXML
    private TableColumn<Cliente, Integer> codigoClienteColumn;
    @FXML
    private TableColumn<Cliente, String> nombreColumn;
    @FXML
    private TableColumn<Cliente, String> generoColumn;
    @FXML
    private TableColumn<Cliente, LocalDate> fechaNacimientoColumn;
    @FXML
    private TableColumn<Cliente, String> duiColumn;
    @FXML
    private TableColumn<Cliente, String> telefonoColumn;

    @FXML
    private Button guardarButton;
    @FXML
    private Button limpiarButton;
    @FXML
    private Button eliminarButton;
    @FXML
    private Button actualizarButton;

    private ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    private boolean isUpdatingTelefono = false;
    private boolean isUpdatingDui = false;

    @FXML
    public void initialize() {
        // Configurar las celdas de la tabla para mostrar los valores de los atributos de Cliente
        codigoClienteColumn.setCellValueFactory(new PropertyValueFactory<>("codigoUsuario"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        generoColumn.setCellValueFactory(new PropertyValueFactory<>("genero"));
        fechaNacimientoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        duiColumn.setCellValueFactory(new PropertyValueFactory<>("dui"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        generoComboBox.setItems(FXCollections.observableArrayList("Masculino", "Femenino"));
        // Asignar la lista de clientes a la TableView
        clientesTableView.setItems(clientes);

        // Convertir las fechas en la columna de fechaNacimiento a un formato legible
        fechaNacimientoColumn.setCellFactory(column -> {
            return new TableCell<Cliente, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.toString()); // Puedes ajustar el formato de fecha aquí si lo deseas
                    }
                }
            };
        });

        // Agregar los listeners para formatear los campos en tiempo real
        telefonoTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdatingTelefono) {
                isUpdatingTelefono = true;
                Platform.runLater(() -> {
                    if (newValue.length() > 9) {
                        telefonoTextField.setText(oldValue);
                    } else {
                        telefonoTextField.setText(formatearTelefono(newValue));
                        telefonoTextField.positionCaret(telefonoTextField.getText().length());
                    }
                    isUpdatingTelefono = false;
                });
            }
        });

        duiTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isUpdatingDui) {
                isUpdatingDui = true;
                Platform.runLater(() -> {
                    if (newValue.length() > 10) {
                        duiTextField.setText(oldValue);
                    } else {
                        duiTextField.setText(formatearDui(newValue));
                        duiTextField.positionCaret(duiTextField.getText().length());
                    }
                    isUpdatingDui = false;
                });
            }
        });

        nombreTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32) {
                nombreTextField.setText(oldValue);
            } else if (!newValue.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                nombreTextField.setText(oldValue);
            }
        });

        cargarClientesDesdeBaseDeDatos();
    }


    private String formatearTelefono(String telefono) {
        telefono = telefono.replaceAll("[^0-9]", ""); // Eliminar caracteres no numéricos
        if (telefono.length() > 4) {
            telefono = telefono.substring(0, 4) + '-' + telefono.substring(4);
        }
        if (telefono.length() > 9) {
            telefono = telefono.substring(0, 9);
        }
        return telefono;
    }

    private String formatearDui(String dui) {
        dui = dui.replaceAll("[^0-9]", ""); // Eliminar caracteres no numéricos
        if (dui.length() > 8) {
            dui = dui.substring(0, 8) + '-' + dui.substring(8);
        }
        if (dui.length() > 10) {
            dui = dui.substring(0, 10);
        }
        return dui;
    }


    private void cargarClientesDesdeBaseDeDatos() {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "SELECT * FROM cliente"; // Ajusta esta consulta según tu esquema de base de datos
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // Limpiar la lista existente de clientes
            clientes.clear();

            // Iterar sobre el resultado y agregar cada cliente a la lista
            while (rs.next()) {
                int codigo = rs.getInt("codigoUsuario");
                String nombre = rs.getString("nombreUsuario");
                String genero = rs.getString("genero");
                LocalDate fechaNacimiento = rs.getDate("fechaNacimiento").toLocalDate(); // Ajusta según el tipo de dato en tu base de datos
                String dui = rs.getString("dui");
                String telefono = rs.getString("telefono");

                // Crear un nuevo objeto Cliente y agregarlo a la lista
                Cliente cliente = new Cliente(codigo, nombre, genero, fechaNacimiento, dui, telefono);
                clientes.add(cliente);
            }

            // Cerrar los recursos
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
        }
    }

    // Método para guardar un cliente
    // Método para guardar un cliente
    @FXML
    void guardarCliente() {
        String nombre = nombreTextField.getText();
        String genero = (generoComboBox.getValue() != null) ? generoComboBox.getValue() : "";
        String telefono = telefonoTextField.getText();
        String dui = duiTextField.getText();
        LocalDate fechaNacimiento = fechaNacimientoDatePicker.getValue();

        // Validar campos vacíos
        if (nombre.isEmpty() || genero.isEmpty() || telefono.isEmpty() || dui.isEmpty() || fechaNacimiento == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validar el formato del nombre
        if (nombre.length() > 50 || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
            mostrarAlerta("Error", "El campo de nombre debe contener solo letras y un máximo de 50 caracteres.");
            return;
        }

        // Validar el formato de Dui y teléfono
        if (!dui.matches("^[0-9]{8}-[0-9]{1}$")) {
            mostrarAlerta("Error", "El campo de Dui debe contener exactamente 9 números y un guión.");
            return;
        }

        if (!telefono.matches("^[0-9]{4}-[0-9]{4}$")) {
            mostrarAlerta("Error", "El campo de teléfono debe contener exactamente 8 números y un guión.");
            return;
        }

        // Crear un objeto Cliente con los datos ingresados
        Cliente cliente = new Cliente(0, nombre, genero, fechaNacimiento, dui, telefono);
        // Guardar el cliente en la base de datos
        if (guardarClienteEnBD(cliente)) {
            mostrarAlerta("Cliente Guardado", "El cliente se ha guardado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo guardar el cliente en la base de datos.");
        }

        limpiarCampos();
        cargarClientesDesdeBaseDeDatos();
    }



    // Método para guardar un cliente en la base de datos
    private boolean guardarClienteEnBD(Cliente cliente) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "INSERT INTO cliente (nombreUsuario, genero, fechaNacimiento, dui, telefono, codigoSucursal) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cliente.getNombreUsuario());
            pstmt.setString(2, cliente.getGenero());
            pstmt.setObject(3, cliente.getFechaNacimiento());
            pstmt.setString(4, cliente.getDui());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setInt(6, 1); // Código de sucursal fijo (1 en este caso)

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            return false;
        }
    }

    // Método para eliminar un cliente
    @FXML
    void eliminarCliente() {
        // Obtener el cliente seleccionado en la tabla
        Cliente clienteSeleccionado = clientesTableView.getSelectionModel().getSelectedItem();

        // Verificar si se ha seleccionado un cliente
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un cliente para eliminar.");
            return;
        }

        // Confirmar la eliminación del cliente
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar este cliente?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Eliminar el cliente de la base de datos
            if (eliminarClienteDeBD(clienteSeleccionado.getCodigoUsuario())) {
                clientes.remove(clienteSeleccionado);
                mostrarAlerta("Cliente Eliminado", "El cliente ha sido eliminado correctamente.");
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el cliente de la base de datos.");
            }
        }
    }

    // Método para eliminar un cliente de la base de datos
    private boolean eliminarClienteDeBD(int codigoCliente) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "DELETE FROM cliente WHERE codigoUsuario = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, codigoCliente);

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            return false;
        }
    }

    // Método para actualizar un cliente
    @FXML
    void actualizarCliente() {
        // Obtener el cliente seleccionado en la tabla
        Cliente clienteSeleccionado = clientesTableView.getSelectionModel().getSelectedItem();

        // Verificar si se ha seleccionado un cliente
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un cliente para actualizar.");
            return;
        }

        // Obtener los datos actualizados de los campos de entrada
        String nombre = nombreTextField.getText();
        String genero = (generoComboBox.getValue() != null) ? generoComboBox.getValue() : "";
        String telefono = telefonoTextField.getText();
        String dui = duiTextField.getText();
        LocalDate fechaNacimiento = fechaNacimientoDatePicker.getValue();

        // Validar campos vacíos
        if (nombre.isEmpty() || genero.isEmpty() || telefono.isEmpty() || dui.isEmpty() || fechaNacimiento == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validar el formato de Dui y teléfono
        if (!dui.matches("^[0-9-]+$")) {
            mostrarAlerta("Error", "El campo de Dui debe contener solo números.");
            return;
        }

        if (!telefono.matches("^[0-9-]+$")) {
            mostrarAlerta("Error", "El campo de teléfono debe contener solo números.");
            return;
        }

        // Actualizar los atributos del cliente seleccionado
        clienteSeleccionado.setNombreUsuario(nombre);
        clienteSeleccionado.setGenero(genero);
        clienteSeleccionado.setTelefono(telefono);
        clienteSeleccionado.setDui(dui);
        clienteSeleccionado.setFechaNacimiento(fechaNacimiento);

        // Actualizar el cliente en la base de datos
        if (actualizarClienteEnBD(clienteSeleccionado)) {
            mostrarAlerta("Cliente Actualizado", "El cliente se ha actualizado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el cliente en la base de datos.");
        }

        limpiarCampos();
        cargarClientesDesdeBaseDeDatos();
    }

    // Método para actualizar un cliente en la base de datos
    private boolean actualizarClienteEnBD(Cliente cliente) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "UPDATE cliente SET nombreUsuario = ?, genero = ?, fechaNacimiento = ?, dui = ?, telefono = ? WHERE codigoUsuario = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cliente.getNombreUsuario());
            pstmt.setString(2, cliente.getGenero());
            pstmt.setObject(3, cliente.getFechaNacimiento());
            pstmt.setString(4, cliente.getDui());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setInt(6, cliente.getCodigoUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            return false;
        }
    }

    // Método para limpiar los campos del formulario
    @FXML
    void limpiarCampos() {
        nombreTextField.clear();
        generoComboBox.setValue(null);
        telefonoTextField.clear();
        duiTextField.clear();
        fechaNacimientoDatePicker.setValue(null);
    }


    // Método para mostrar una alerta
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
        stage.setResizable(true);


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
