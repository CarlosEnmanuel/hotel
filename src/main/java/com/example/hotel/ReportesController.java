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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class ReportesController {

    private int contadorReporte = 1; // Inicializamos el contador de reporte

    @FXML
    private void generarReportecita(ActionEvent event) {
        // Ruta base donde deseas guardar el archivo PDF
        String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "pdf" + File.separator;

        // Subcarpeta específica para clientes
        String subcarpetaCliente = "cliente";

        // Ruta completa a la subcarpeta de clientes
        String rutaDirectorioPDF = rutaBase + subcarpetaCliente + File.separator;

        // Crear las carpetas si no existen
        try {
            Path path = Paths.get(rutaDirectorioPDF);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al crear directorio: " + e.getMessage());
            return;
        }

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
        // Ruta base donde deseas guardar el archivo PDF
        String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "pdf" + File.separator;

        // Subcarpeta específica para reservaciones
        String subcarpetaReservaciones = "reservaciones";

        // Ruta completa a la subcarpeta de reservaciones
        String rutaDirectorioPDF = rutaBase + subcarpetaReservaciones + File.separator;

        // Crear las carpetas si no existen
        try {
            Path path = Paths.get(rutaDirectorioPDF);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al crear directorio: " + e.getMessage());
            return;
        }

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
            Table table = new Table(7).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Reservacion")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Usuario")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Habitacion")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Servicio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Horario")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Frecuencias")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Total")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            while (resultSet.next()) {
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("coodigoReservacion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoUsuario"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoHabitacion"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoServicio"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoHorario"))).setTextAlignment(TextAlignment.CENTER));
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
        // Ruta base donde deseas guardar el archivo PDF
        String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "pdf" + File.separator;

        // Subcarpeta específica para empleados
        String subcarpetaEmpleados = "empleados";

        // Ruta completa a la subcarpeta de empleados
        String rutaDirectorioPDF = rutaBase + subcarpetaEmpleados + File.separator;

        // Crear las carpetas si no existen
        try {
            Path path = Paths.get(rutaDirectorioPDF);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al crear directorio: " + e.getMessage());
            return;
        }

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

            String sql = "SELECT codigoEmpleado, nombreEmpleado, genero, fechaNacimiento, dui, telefono, cargo FROM empleado";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(7).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Código de Empleado")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Nombre")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Género")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha de Nacimiento")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("DUI")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Teléfono")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Cargo")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            while (resultSet.next()) {
                table.addCell(new Cell().add(new Paragraph(resultSet.getString("codigoEmpleado"))).setTextAlignment(TextAlignment.CENTER));
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
        // Ruta base donde deseas guardar el archivo PDF
        String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "pdf" + File.separator;

        // Subcarpeta específica para horarios
        String subcarpetaHorarios = "horarios";

        // Ruta completa a la subcarpeta de horarios
        String rutaDirectorioPDF = rutaBase + subcarpetaHorarios + File.separator;

        // Crear las carpetas si no existen
        try {
            Path path = Paths.get(rutaDirectorioPDF);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al crear directorio: " + e.getMessage());
            return;
        }

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

            String sql = "SELECT fechaEntrada, horaEntrada, fechaSalida, horaSalida FROM horarios";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(4).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha Entrada")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Hora Entrada")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha Salida")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Hora Salida")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

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
        // Ruta base donde deseas guardar el archivo PDF
        String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "pdf" + File.separator;

        // Subcarpeta específica para habitaciones
        String subcarpetaHabitaciones = "habitaciones";

        // Ruta completa a la subcarpeta de habitaciones
        String rutaDirectorioPDF = rutaBase + subcarpetaHabitaciones + File.separator;

        // Crear las carpetas si no existen
        try {
            Path path = Paths.get(rutaDirectorioPDF);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al crear directorio: " + e.getMessage());
            return;
        }

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

            String sql = "SELECT codigoHabitacion, tipoDeHabitacion, descripcion, precio FROM habitaciones";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(4).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Código Habitación")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Tipo")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Descripción")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

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
    private void generarReporteservicios(ActionEvent event) {
        // Ruta base donde deseas guardar el archivo PDF
        String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "pdf" + File.separator;

        // Subcarpeta específica para servicios
        String subcarpetaServicios = "servicios";

        // Ruta completa a la subcarpeta de servicios
        String rutaDirectorioPDF = rutaBase + subcarpetaServicios + File.separator;

        // Crear las carpetas si no existen
        try {
            Path path = Paths.get(rutaDirectorioPDF);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al crear directorio: " + e.getMessage());
            return;
        }

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

            String sql = "SELECT codigoServicio, tipoServicio, costo FROM servicios";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(3).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Código Servicio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Tipo de Servicio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Costo")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

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
    private void generarReporteproveedores(ActionEvent event) {
        // Ruta base donde deseas guardar el archivo PDF
        String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "pdf" + File.separator;

        // Subcarpeta específica para proveedores
        String subcarpetaProveedores = "proveedores";

        // Ruta completa a la subcarpeta de proveedores
        String rutaDirectorioPDF = rutaBase + subcarpetaProveedores + File.separator;

        // Crear las carpetas si no existen
        try {
            Path path = Paths.get(rutaDirectorioPDF);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al crear directorio: " + e.getMessage());
            return;
        }

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
            Paragraph encabezado = new Paragraph("Reporte de Proveedores")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(encabezado);

            // Espacio entre el encabezado y la tabla
            document.add(new Paragraph("\n"));

            String sql = "SELECT codigoProveedor, nombreEmpresa, telefono, direccion, nombreContacto, codigoSucursal FROM proveedores";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Crear una tabla para mostrar los datos
            Table table = new Table(6).useAllAvailableWidth();
            table.setTextAlignment(TextAlignment.CENTER);

            // Añadir encabezados a la tabla
            table.addHeaderCell(new Cell().add(new Paragraph("Código Proveedor")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Empresa")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Teléfono")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Dirección")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Contacto")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Código Sucursal")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));

            while (resultSet.next()) {
                String codigoProveedor = resultSet.getString("codigoProveedor");
                String nombreEmpresa = resultSet.getString("nombreEmpresa");
                String telefono = resultSet.getString("telefono");
                String direccion = resultSet.getString("direccion");
                String nombreContacto = resultSet.getString("nombreContacto");
                String codigoSucursal = resultSet.getString("codigoSucursal");

                // Asegurarse de que los valores no sean nulos antes de agregarlos a la tabla
                table.addCell(new Cell().add(new Paragraph(codigoProveedor != null ? codigoProveedor : "")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(nombreEmpresa != null ? nombreEmpresa : "")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(telefono != null ? telefono : "")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(direccion != null ? direccion : "")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(nombreContacto != null ? nombreContacto : "")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(codigoSucursal != null ? codigoSucursal : "")).setTextAlignment(TextAlignment.CENTER));
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
