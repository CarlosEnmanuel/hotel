package com.example.hotel;

import com.example.hotel.Conexion;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
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


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class ReportesController {

    private int contadorReporte = 1; // Inicializamos el contador de reporte

    @FXML
    private void generarReportecita(ActionEvent event) {
        // Ruta donde deseas guardar el archivo PDF
        String rutaDirectorioPDF = "C:\\Users\\ricar\\Desktop\\pdf\\citas\\";

        String nombreArchivoPDF;

        Connection connection = null;

        try {
            connection = Conexion.getConnection();
            File pdfFile;

            do {
                nombreArchivoPDF = "cliente" + contadorReporte + ".pdf";
                pdfFile = new File(rutaDirectorioPDF + nombreArchivoPDF);
                contadorReporte++;
            } while (pdfFile.exists()); // Comprobar si el archivo ya existe

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar encabezado
            Paragraph encabezado = new Paragraph("Reporte de Clientes")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT codigoUsuario, nombreUsuario, genero, fechaNacimiento, dui, telefono FROM cliente";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(6).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Código de usuario")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Nombre de usuario")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Género")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha de nacimiento")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("DUI")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Teléfono")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            while (resultSet.next()) {
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoUsuario"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("nombreUsuario"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("genero"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("fechaNacimiento"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("dui"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("telefono"))).setTextAlignment(TextAlignment.CENTER));
            }
            document.add(table);

            resultSet.close();
            statement.close();
            document.close();
            mostrarMensaje("PDF generado con éxito en: " + rutaDirectorioPDF + nombreArchivoPDF);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el informe PDF: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void generarReportereservaciones(ActionEvent event) {
        // Ruta donde deseas guardar el archivo PDF
        String rutaDirectorioPDF = "C:\\Users\\ricar\\Desktop\\pdf\\reservaciones\\";

        String nombreArchivoPDF;

        Connection connection = null;

        try {
            connection = Conexion.getConnection();
            File pdfFile;

            do {
                nombreArchivoPDF = "reservaciones" + contadorReporte + ".pdf";
                pdfFile = new File(rutaDirectorioPDF + nombreArchivoPDF);
                contadorReporte++;
            } while (pdfFile.exists()); // Comprobar si el archivo ya existe

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar encabezado
            Paragraph encabezado = new Paragraph("Reporte de reservaciones")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT coodigoReservacion, codigoUsuario, codigoHabitacion, codigoServicio, codigoHorario, frecuencias, total FROM reservaciones";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(6).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph(" Reservacion")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Usuario")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Habitacion ")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Servicio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Horario")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Frecuencias")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("total")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));


            while (resultSet.next()) {
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("coodigoReservacion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoUsuario"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoHabitacion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoServicio"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoHorario "))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("frecuencias"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("total"))).setTextAlignment(TextAlignment.CENTER));
            }
            document.add(table);

            resultSet.close();
            statement.close();
            document.close();
            mostrarMensaje("PDF generado con éxito en: " + rutaDirectorioPDF + nombreArchivoPDF);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el informe PDF: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void generarReporteempleados(ActionEvent event) {
        // Ruta donde deseas guardar el archivo PDF
        String rutaDirectorioPDF = "C:\\Users\\ricar\\Desktop\\pdf\\empleados\\";

        String nombreArchivoPDF;

        Connection connection = null;

        try {
            connection = Conexion.getConnection();
            File pdfFile;

            do {
                nombreArchivoPDF = "empleados" + contadorReporte + ".pdf";
                pdfFile = new File(rutaDirectorioPDF + nombreArchivoPDF);
                contadorReporte++;
            } while (pdfFile.exists()); // Comprobar si el archivo ya existe

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar encabezado
            Paragraph encabezado = new Paragraph("Reporte de Empleados")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT codigoEmpleado, nombreEmpleado,genero,fechaNacimiento,dui,telefono,cargo FROM empleado";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(6).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla

            table.addHeaderCell(new Cell().add(new Paragraph(" Nombre")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Genero ")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Nacimiento")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Dui")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Telefono")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Cargo")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));


            while (resultSet.next()) {

                table.addCell(new Cell().add(new Paragraph(resultSet.getString("nombreEmpleado"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("genero"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("fechaNacimiento"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("dui"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("telefono"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("cargo"))).setTextAlignment(TextAlignment.CENTER));
            }
            document.add(table);

            resultSet.close();
            statement.close();
            document.close();
            mostrarMensaje("PDF generado con éxito en: " + rutaDirectorioPDF + nombreArchivoPDF);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el informe PDF: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void generarReportehorarios(ActionEvent event) {
        // Ruta donde deseas guardar el archivo PDF
        String rutaDirectorioPDF = "C:\\Users\\ricar\\Desktop\\pdf\\horarios\\";

        String nombreArchivoPDF;

        Connection connection = null;

        try {
            connection = Conexion.getConnection();
            File pdfFile;

            do {
                nombreArchivoPDF = "horarios" + contadorReporte + ".pdf";
                pdfFile = new File(rutaDirectorioPDF + nombreArchivoPDF);
                contadorReporte++;
            } while (pdfFile.exists()); // Comprobar si el archivo ya existe

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar encabezado
            Paragraph encabezado = new Paragraph("Reporte de Horarios")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT fechaEntrada, horaEntrada,fechaSalida,horaSalida FROM horarios";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(4).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla

            table.addHeaderCell(new Cell().add(new Paragraph(" FechaEntrada")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" HoraEntrada ")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" FechaSalida")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" HoraSalida")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            while (resultSet.next()) {

                table.addCell(new Cell().add(new Paragraph(resultSet.getString("fechaEntrada"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("horaEntrada"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("fechaSalida"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("horaSalida"))).setTextAlignment(TextAlignment.CENTER));
            }
            document.add(table);

            resultSet.close();
            statement.close();
            document.close();
            mostrarMensaje("PDF generado con éxito en: " + rutaDirectorioPDF + nombreArchivoPDF);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el informe PDF: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void generarReportehabitaciones(ActionEvent event) {
        // Ruta donde deseas guardar el archivo PDF
        String rutaDirectorioPDF = "C:\\Users\\ricar\\Desktop\\pdf\\habitaciones\\";

        String nombreArchivoPDF;

        Connection connection = null;

        try {
            connection = Conexion.getConnection();
            File pdfFile;

            do {
                nombreArchivoPDF = "habitaciones" + contadorReporte + ".pdf";
                pdfFile = new File(rutaDirectorioPDF + nombreArchivoPDF);
                contadorReporte++;
            } while (pdfFile.exists()); // Comprobar si el archivo ya existe

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar encabezado
            Paragraph encabezado = new Paragraph("Reporte de Habitaciones")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT codigoHabitacion, tipoDeHabitacion,descripcion,precio FROM habitaciones";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(4).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla

            table.addHeaderCell(new Cell().add(new Paragraph(" cod Habitacion")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" tipo ")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" descripcion")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" precio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            while (resultSet.next()) {

                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoHabitacion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("tipoDeHabitacion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("descripcion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("precio"))).setTextAlignment(TextAlignment.CENTER));
            }
            document.add(table);

            resultSet.close();
            statement.close();
            document.close();
            mostrarMensaje("PDF generado con éxito en: " + rutaDirectorioPDF + nombreArchivoPDF);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el informe PDF: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void generarReporteservicios (ActionEvent event) {
        // Ruta donde deseas guardar el archivo PDF
        String rutaDirectorioPDF = "C:\\Users\\ricar\\Desktop\\pdf\\servicios\\";

        String nombreArchivoPDF;

        Connection connection = null;

        try {
            connection = Conexion.getConnection();
            File pdfFile;

            do {
                nombreArchivoPDF = "servicios" + contadorReporte + ".pdf";
                pdfFile = new File(rutaDirectorioPDF + nombreArchivoPDF);
                contadorReporte++;
            } while (pdfFile.exists()); // Comprobar si el archivo ya existe

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar encabezado
            Paragraph encabezado = new Paragraph("Reporte de Servicios")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT codigoServicio, tipoServicio,costo FROM servicios";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(4).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla

            table.addHeaderCell(new Cell().add(new Paragraph(" cod servicio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" tipo de servicio ")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" costo")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));


            while (resultSet.next()) {

                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoServicio"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("tipoServicio"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("costo"))).setTextAlignment(TextAlignment.CENTER));

            }
            document.add(table);

            resultSet.close();
            statement.close();
            document.close();
            mostrarMensaje("PDF generado con éxito en: " + rutaDirectorioPDF + nombreArchivoPDF);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el informe PDF: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void generarReporteproveedores (ActionEvent event) {
        // Ruta donde deseas guardar el archivo PDF
        String rutaDirectorioPDF = "C:\\Users\\ricar\\Desktop\\pdf\\proveedores\\";

        String nombreArchivoPDF;

        Connection connection = null;

        try {
            connection = Conexion.getConnection();
            File pdfFile;

            do {
                nombreArchivoPDF = "proveedores" + contadorReporte + ".pdf";
                pdfFile = new File(rutaDirectorioPDF + nombreArchivoPDF);
                contadorReporte++;
            } while (pdfFile.exists()); // Comprobar si el archivo ya existe

            PdfWriter pdfWriter = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Agregar encabezado
            Paragraph encabezado = new Paragraph("Reporte de proveedores")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT codigoProveedor, nombreEmpresa,telefono,direccion,nombreContacto,codigoSucursal FROM proveedores";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(6).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla

            table.addHeaderCell(new Cell().add(new Paragraph(" cod proveedor")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Empresa")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Telefono")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Direccion")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph(" Contacto")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));


            while (resultSet.next()) {

                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoProveedor"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("nombreEmpresa"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("telefono"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("direccion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("nombreContacto"))).setTextAlignment(TextAlignment.CENTER));

            }
            document.add(table);

            resultSet.close();
            statement.close();
            document.close();
            mostrarMensaje("PDF generado con éxito en: " + rutaDirectorioPDF + nombreArchivoPDF);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el informe PDF: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error en la consulta SQL: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
        // Implementa un cuadro de diálogo o notificación para mostrar el mensaje.
        // Puedes utilizar Alert u otro componente según tus preferencias.
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
