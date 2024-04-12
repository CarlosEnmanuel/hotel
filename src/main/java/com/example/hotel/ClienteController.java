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
    private MenuButton generoMenuButton;
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


    public void initialize() {
        // Configurar las celdas de la tabla para mostrar los valores de los atributos de Cliente
        codigoClienteColumn.setCellValueFactory(new PropertyValueFactory<>("codigoUsuario"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        generoColumn.setCellValueFactory(new PropertyValueFactory<>("genero"));
        fechaNacimientoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        duiColumn.setCellValueFactory(new PropertyValueFactory<>("dui"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));

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

        cargarClientesDesdeBaseDeDatos();

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
    @FXML
    void guardarCliente() {
        String nombre = nombreTextField.getText();
        String genero = generoMenuButton.getText();
        String telefono = telefonoTextField.getText();
        String dui = duiTextField.getText();
        LocalDate fechaNacimiento = fechaNacimientoDatePicker.getValue();

        // Validar campos vacíos
        if (nombre.isEmpty() || genero.isEmpty() || telefono.isEmpty() || dui.isEmpty() || fechaNacimiento == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validar el formato de Dui y teléfono
        if (!dui.matches("^[0-9 -]+$")) {
            mostrarAlerta("Error", "El campo de Dui debe contener solo números.");
            return;
        }

        if (!telefono.matches("^[0-9 -]+$")) {
            mostrarAlerta("Error", "El campo de teléfono debe contener solo números.");
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
    private void eliminarCliente() {
        // Obtener el cliente seleccionado en la tabla
        Cliente clienteSeleccionado = clientesTableView.getSelectionModel().getSelectedItem();

        // Verificar si se ha seleccionado un cliente
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un cliente para eliminar.");
            return;
        }

        // Mostrar un cuadro de diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este cliente?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // El usuario seleccionó OK, proceder con la eliminación del cliente

            try {
                Connection conn = Conexion.getConnection();
                String sql = "DELETE FROM cliente WHERE codigoUsuario = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, clienteSeleccionado.getCodigoUsuario());

                int filasAfectadas = pstmt.executeUpdate();

                pstmt.close();
                conn.close();

                // Verificar si se eliminó correctamente el cliente
                if (filasAfectadas > 0) {
                    // Eliminar el cliente de la lista observable
                    clientes.remove(clienteSeleccionado);
                    mostrarAlerta("Cliente Eliminado", "El cliente se ha eliminado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el cliente.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            }
        }
    }


    // Método para actualizar un cliente
    @FXML
    private void actualizarCliente() {
        // Obtener el cliente seleccionado en la tabla
        Cliente clienteSeleccionado = clientesTableView.getSelectionModel().getSelectedItem();

        // Verificar si se ha seleccionado un cliente
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un cliente para actualizar.");
            return;
        }

        // Obtener los nuevos datos del cliente desde los campos de entrada
        String nuevoNombre = nombreTextField.getText();
        String nuevoGenero = generoMenuButton.getText();
        String nuevoTelefono = telefonoTextField.getText();
        String nuevoDui = duiTextField.getText();
        LocalDate nuevaFechaNacimiento = fechaNacimientoDatePicker.getValue();

        // Validar campos vacíos
        if (nuevoNombre.isEmpty() || nuevoGenero.isEmpty() || nuevoTelefono.isEmpty() || nuevoDui.isEmpty() || nuevaFechaNacimiento == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validar el formato de Dui y teléfono
        if (!nuevoDui.matches("^[0-9 -]+$")) {
            mostrarAlerta("Error", "El campo de Dui debe contener solo números.");
            return;
        }

        if (!nuevoTelefono.matches("^[0-9 -]+$")) {
            mostrarAlerta("Error", "El campo de teléfono debe contener solo números.");
            return;
        }

        // Crear un objeto Cliente con los nuevos datos
        Cliente clienteActualizado = new Cliente(
                clienteSeleccionado.getCodigoUsuario(),
                nuevoNombre,
                nuevoGenero,
                nuevaFechaNacimiento,
                nuevoDui,
                nuevoTelefono
        );

        try {
            Connection conn = Conexion.getConnection();
            String sql = "UPDATE cliente SET nombreUsuario = ?, genero = ?, fechaNacimiento = ?, dui = ?, telefono = ? WHERE codigoUsuario = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clienteActualizado.getNombreUsuario());
            pstmt.setString(2, clienteActualizado.getGenero());
            pstmt.setObject(3, clienteActualizado.getFechaNacimiento());
            pstmt.setString(4, clienteActualizado.getDui());
            pstmt.setString(5, clienteActualizado.getTelefono());
            pstmt.setInt(6, clienteActualizado.getCodigoUsuario());

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            // Verificar si se actualizó correctamente el cliente
            if (filasAfectadas > 0) {
                // Actualizar el cliente en la lista observable
                int indiceCliente = clientes.indexOf(clienteSeleccionado);
                clientes.set(indiceCliente, clienteActualizado);
                mostrarAlerta("Cliente Actualizado", "El cliente se ha actualizado correctamente.");
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el cliente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
        }

        limpiarCampos();
    }


    // Método para limpiar los campos de entrada
    @FXML
    private void limpiarCampos() {
        nombreTextField.clear();
        telefonoTextField.clear();
        duiTextField.clear();
        fechaNacimientoDatePicker.setValue(null); // Limpiar también el valor seleccionado en el DatePicker
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
