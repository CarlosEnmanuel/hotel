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

public class EmpleadosController {

    @FXML
    private TextField nombresEmpleadoTextField;

    @FXML
    private DatePicker fechaNacimientoDatePicker;
    @FXML
    private ComboBox<String> generoComboBox;
    @FXML
    private TextField duiTextField;
    @FXML
    private TextField cargoTextField;
    @FXML
    private TextField telefonoTextField;

    @FXML
    private TableView<Empleado> empleadosTableView;
    @FXML
    private TableColumn<Empleado, Integer> codigoEmpleadoColumn;
    @FXML
    private TableColumn<Empleado, String> nombresEmpleadoColumn;
    @FXML
    private TableColumn<Empleado, LocalDate> fechaNacimientoColumn;
    @FXML
    private TableColumn<Empleado, String> generoColumn;
    @FXML
    private TableColumn<Empleado, String> duiColumn;
    @FXML
    private TableColumn<Empleado, String> telefonoColumn;
    @FXML
    private TableColumn<Empleado, String> cargoColumn;


    @FXML
    private Button guardarEmpleadoButton;
    @FXML
    private Button eliminarEmpleadoButton;
    @FXML
    private Button actualizarEmpleadoButton;
    @FXML
    private Button limpiarEmpleadoButton;

    private ObservableList<Empleado> empleados = FXCollections.observableArrayList();

    public void initialize() {
        codigoEmpleadoColumn.setCellValueFactory(new PropertyValueFactory<>("codigoEmpleado"));
        nombresEmpleadoColumn.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));
        fechaNacimientoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        generoColumn.setCellValueFactory(new PropertyValueFactory<>("genero"));
        duiColumn.setCellValueFactory(new PropertyValueFactory<>("dui"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        cargoColumn.setCellValueFactory(new PropertyValueFactory<>("cargo"));

        generoComboBox.setItems(FXCollections.observableArrayList("Masculino", "Femenino"));

        empleadosTableView.setItems(empleados);

        cargarEmpleadosDesdeBaseDeDatos();
    }

    private void cargarEmpleadosDesdeBaseDeDatos() {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "SELECT * FROM empleado";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            empleados.clear();

            while (rs.next()) {
                int codigoEmpleado = rs.getInt("codigoEmpleado");
                String nombresEmpleado = rs.getString("nombreEmpleado");
                LocalDate fechaNacimiento = rs.getDate("fechaNacimiento").toLocalDate();
                String genero = rs.getString("genero");
                String dui = rs.getString("dui");
                String telefono = rs.getString("telefono");
                String cargo = rs.getString("cargo");

                Empleado empleado = new Empleado(codigoEmpleado, nombresEmpleado, fechaNacimiento, genero, dui, telefono, cargo);
                empleados.add(empleado);
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
    void guardarEmpleado() {
        String nombresEmpleado = nombresEmpleadoTextField.getText();
        LocalDate fechaNacimiento = fechaNacimientoDatePicker.getValue();
        String genero = generoComboBox.getValue();
        String dui = duiTextField.getText();
        String telefono = telefonoTextField.getText();
        String cargo = cargoTextField.getText();

        if (nombresEmpleado.isEmpty() || fechaNacimiento == null || genero.isEmpty() || dui.isEmpty() || telefono.isEmpty() || cargo.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        Empleado empleado = new Empleado(0, nombresEmpleado, fechaNacimiento, genero, dui, telefono, cargo);
        if (guardarEmpleadoEnBD(empleado)) {
            mostrarAlerta("Empleado Guardado", "El empleado se ha guardado correctamente en la base de datos.");
        } else {
            mostrarAlerta("Error", "No se pudo guardar el empleado en la base de datos.");
        }

        limpiarCampos();
        cargarEmpleadosDesdeBaseDeDatos();
    }

    private boolean guardarEmpleadoEnBD(Empleado empleado) {
        try {
            Connection conn = Conexion.getConnection();
            String sql = "INSERT INTO empleado (nombreEmpleado, fechaNacimiento, genero, dui, telefono, cargo) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, empleado.getNombreEmpleado());
            pstmt.setObject(2, empleado.getFechaNacimiento());
            pstmt.setString(3, empleado.getGenero());
            pstmt.setString(4, empleado.getDui());
            pstmt.setString(5, empleado.getTelefono());
            pstmt.setString(6, empleado.getCargo());

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

    @FXML
    void eliminarEmpleado() {
        Empleado empleadoSeleccionado = empleadosTableView.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un empleado para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este empleado?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection conn = Conexion.getConnection();
                String sql = "DELETE FROM empleado WHERE codigoEmpleado = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, empleadoSeleccionado.getCodigoEmpleado());

                int filasAfectadas = pstmt.executeUpdate();

                pstmt.close();
                conn.close();

                if (filasAfectadas > 0) {
                    empleados.remove(empleadoSeleccionado);
                    mostrarAlerta("Empleado Eliminado", "El empleado se ha eliminado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar el empleado.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
            }
        }
    }

    @FXML
    void actualizarEmpleado() {
        Empleado empleadoSeleccionado = empleadosTableView.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un empleado para actualizar.");
            return;
        }

        String nombresEmpleado = nombresEmpleadoTextField.getText();
        LocalDate fechaNacimiento = fechaNacimientoDatePicker.getValue();
        String genero = generoComboBox.getValue();
        String dui = duiTextField.getText();
        String telefono = telefonoTextField.getText();
        String cargo = cargoTextField.getText();

        if (nombresEmpleado.isEmpty() || fechaNacimiento == null || genero.isEmpty() || dui.isEmpty() || telefono.isEmpty() || cargo.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        Empleado empleadoActualizado = new Empleado(empleadoSeleccionado.getCodigoEmpleado(), nombresEmpleado, fechaNacimiento, genero, dui, telefono, cargo);

        try {
            Connection conn = Conexion.getConnection();
            String sql = "UPDATE empleado SET nombreEmpleado = ?, fechaNacimiento = ?, genero = ?, dui = ?, telefono = ?, cargo = ? WHERE codigoEmpleado = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, empleadoActualizado.getNombreEmpleado());
            pstmt.setObject(2, empleadoActualizado.getFechaNacimiento());
            pstmt.setString(3, empleadoActualizado.getGenero());
            pstmt.setString(4, empleadoActualizado.getDui());
            pstmt.setString(5, empleadoActualizado.getTelefono());
            pstmt.setString(6, empleadoActualizado.getCargo());
            pstmt.setInt(7, empleadoActualizado.getCodigoEmpleado());

            int filasAfectadas = pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            if (filasAfectadas > 0) {
                int indiceEmpleado = empleados.indexOf(empleadoSeleccionado);
                empleados.set(indiceEmpleado, empleadoActualizado);
                mostrarAlerta("Empleado Actualizado", "El empleado se ha actualizado correctamente.");
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el empleado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "Hubo un error al conectar con la base de datos.");
        }

        limpiarCampos();
    }

    @FXML
    void limpiarCampos() {
        nombresEmpleadoTextField.clear();
        fechaNacimientoDatePicker.setValue(null);
        generoComboBox.getSelectionModel().clearSelection();
        duiTextField.clear();
        telefonoTextField.clear();
        cargoTextField.clear();
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
        cargarVista("Inicio.fxml");
    }

    @FXML
    private void irACliente(ActionEvent event) throws IOException {
        cargarVista("Cliente.fxml");
    }

    @FXML
    private void irAReservaciones(ActionEvent event) throws IOException {
        cargarVista("Reservaciones.fxml");
    }

    @FXML
    private void irAEmpleados(ActionEvent event) throws IOException {
        cargarVista("Empleados.fxml");
    }

    @FXML
    private void irAHorarios(ActionEvent event) throws IOException {
        cargarVista("Horario.fxml");
    }

    @FXML
    private void irAHabitaciones(ActionEvent event) throws IOException {
        cargarVista("Habitaciones.fxml");
    }

    @FXML
    private void irAServicios(ActionEvent event) throws IOException {
        cargarVista("Servicios.fxml");
    }

    @FXML
    private void irAProveedores(ActionEvent event) throws IOException {
        cargarVista("Proveedores.fxml");
    }

    @FXML
    private void irAReportes(ActionEvent event) throws IOException {
        cargarVista("Reportes.fxml");
    }

    public void userLogOut(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de salida");
        alert.setHeaderText("¿Está seguro de que desea salir del sitio?");
        alert.setContentText("Seleccione Aceptar para salir o Cancelar para continuar.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    private void cargarVista(String vista) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) nombresEmpleadoTextField).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
