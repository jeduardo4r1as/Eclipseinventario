package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.FacturaDAO;
import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.DetalleFactura;
import com.inventory.appinventario.model.Factura;
import com.inventory.appinventario.model.Usuario;
import com.inventory.appinventario.util.ConexionBD;
import com.inventory.appinventario.util.ItemFacturaDTO;
import com.inventory.appinventario.util.Metodos;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;

import java.awt.image.BufferedImage;
import java.io.*;
//import java.net.Authenticator;
//import java.net.PasswordAuthentication;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

//para que los valores se vean en peso colombiano
import java.text.NumberFormat;

import javafx.scene.control.TableCell;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.Notifications;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class FacturaController  implements Initializable {

    @FXML
    private Button btnListar;

    @FXML
    private Button btnVerFacturas;

    @FXML
    private Button btnExcel;

    @FXML
    private Button btnPdf;

    @FXML
    private Button btnCuadre;

    @FXML
    private TextField cjBuscar;

    @FXML
    private TableView<Factura> tablaProductos;

    @FXML
    private TableColumn<Factura, String> colCodigo;
    @FXML
    private TableColumn<Factura, String> colNombre;
    @FXML
    private TableColumn<Factura, String> colCorreo;
    @FXML
    private TableColumn<Factura, Number> colTotal;
    @FXML
    private TableColumn<Factura, String> colFechaVenta;

    @FXML
    private TableColumn<Factura, Number> colSubTotal;
    @FXML
    private TableColumn<Factura, String> colVendedor;

    private ConexionBD conexionBD;

    @FXML
    private AnchorPane root;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Formato moneda colombiano sin decimales
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formatoMoneda.setMaximumFractionDigits(0);

        // 1. Configurar las columnas ────────────────
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreCliente())
        );
        colCorreo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCorreo())
        );
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNumeroFactura())
        );
        colFechaVenta.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaDeVenta())
        );
        colTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getTotal())
        );

        colVendedor.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVendedor())
        );

        colSubTotal.setCellValueFactory(data ->
               new SimpleDoubleProperty(data.getValue().getSubTotal())
       );

        // 2. Obtener los datos desde el DAO ─────────
        FacturaDAO facturaDAO = new FacturaDAO(new ConexionBD());

        try {
            // El DAO ya hace el INNER JOIN internamente
            ObservableList<Factura> listaFacturas =
                    FXCollections.observableArrayList(facturaDAO.listarFacturas());

            tablaProductos.setItems(listaFacturas);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void buscarProductoKeyReleased(KeyEvent event) {
        String filtro = cjBuscar.getText().toLowerCase().trim();

        // Obtener todas las facturas
        FacturaDAO facturaDAO = new FacturaDAO(new ConexionBD());

        try {
            List<Factura> listaOriginal = facturaDAO.listarFacturas(); // Método que devuelve todas las facturas

            // Filtrar por nombre de cliente o número de factura
            List<Factura> filtradas = listaOriginal.stream()
                    .filter(f -> f.getNombreCliente().toLowerCase().contains(filtro) ||
                            f.getNumeroFactura().toLowerCase().contains(filtro))
                    .toList();

            tablaProductos.setItems(FXCollections.observableArrayList(filtradas));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void listarFacturas(ActionEvent event) {
        FacturaDAO facturaDAO = new FacturaDAO(new ConexionBD());

        try {
            // El DAO ya hace el INNER JOIN internamente
            ObservableList<Factura> listaFacturas =
                    FXCollections.observableArrayList(facturaDAO.listarFacturas());

            tablaProductos.setItems(listaFacturas);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void verFacturas(ActionEvent event) {

        Factura facturaSeleccionada = tablaProductos.getSelectionModel().getSelectedItem();

        if (facturaSeleccionada == null) {
            org.controlsfx.control.Notifications.create()
                    .title("Factura")
                    .text("Debes seleccionar una factura.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showInformation();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/FacturaDetalle.fxml"));
            Parent root = loader.load();
            // Pasar el número de factura al controlador del detalle
            FacturaDetalleController controller = loader.getController();
            controller.cargarFacturaDetalle(Integer.parseInt(String.valueOf(Integer.parseInt(facturaSeleccionada.getNumeroFactura()))));

            Stage stage = new Stage();
            stage.setTitle("#" + facturaSeleccionada.getNumeroFactura());
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/add_user.png")));
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void enviarFactura(ActionEvent event) {
        Factura facturaSeleccionada = tablaProductos.getSelectionModel().getSelectedItem();

        if (facturaSeleccionada == null) {
            org.controlsfx.control.Notifications.create()
                    .title("Factura")
                    .text("Debes seleccionar una factura.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showInformation();
            return;
        }

        try {
            // 1. Generar PDF
            File archivoPdf = generarFacturaPdf(facturaSeleccionada);

            // 2. Correo del cliente (ajústalo si se obtiene de otro lado)
            String correoDestino = facturaSeleccionada.getCorreo();
            String emailDestino = correoDestino; // puedes obtenerlo de facturaSeleccionada si lo tienes

            if (emailDestino == null || emailDestino.isEmpty()) {
                System.out.println("⚠️ El cliente no tiene correo registrado.");
                org.controlsfx.control.Notifications.create()
                        .title("Correo")
                        .text("El cliente no tiene correo registrado.")
                        .position(Pos.CENTER)
                        .hideAfter(Duration.seconds(5))
                        .showInformation();
                return;
            }

            // 3. Enviar
            enviarCorreoConAdjunto(
                    emailDestino,
                    "Factura de su compra ECLIPSE",
                    "Estimado cliente, adjuntamos la factura de su compra. Gracias por confiar en nosotros.",
                    archivoPdf
            );

            org.controlsfx.control.Notifications.create()
                    .title("Factura")
                    .text("La factura fue enviada exitosamente al correo del cliente.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showInformation();

        } catch (Exception e) {
            e.printStackTrace();
            org.controlsfx.control.Notifications.create()
                    .title("Error")
                    .text("No se pudo enviar la factura. Verifica la conexión o el correo.")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .showError();
        }
    }




    public File generarFacturaPdf(Factura factura) throws Exception {
        String numeroFactura = factura.getNumeroFactura();
        List<ItemFacturaDTO> productos = new ArrayList<>();

        String clienteNombre = "";
        String clienteDireccion = "";
        String clienteTelefono = "";
        String fechaVenta = "";
        double totalVenta = 0;

        String sql = """
        SELECT c.nombrecliente, c.telefonocliente, c.direccioncliente, 
               v.fechadeventa, v.total,
               p.nombreproducto, p.descripcion, p.talla,
               dv.cantidad, dv.preciodeventa, dv.tipoprecio
        FROM venta v
        INNER JOIN cliente c ON c.idcliente = v.idcliente
        INNER JOIN detalleventa dv ON dv.idventa = v.idventa
        INNER JOIN producto p ON p.idproducto = dv.idproducto
        WHERE v.numerofactura = ?
    """;

        conexionBD = new ConexionBD();
        conexionBD.conectar();

        try (Connection conn = conexionBD.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(numeroFactura));
            ResultSet rs = pst.executeQuery();

            boolean firstRow = true;

            while (rs.next()) {
                if (firstRow) {
                    clienteNombre = rs.getString("nombrecliente");
                    clienteDireccion = rs.getString("direccioncliente");
                    clienteTelefono = rs.getString("telefonocliente");
                    fechaVenta = rs.getString("fechadeventa");
                    totalVenta = rs.getDouble("total");
                    firstRow = false;
                }

                productos.add(new ItemFacturaDTO(
                        rs.getDouble("cantidad"),
                        rs.getString("descripcion"),
                        rs.getString("talla"),
                        rs.getDouble("preciodeventa"),
                        rs.getDouble("cantidad") * rs.getDouble("preciodeventa")
                ));
            }
        }

        // Cargar reporte
        InputStream input = getClass().getResourceAsStream("/reports/FacturaEclipse.jrxml");
        JasperReport reporte = JasperCompileManager.compileReport(input);

        JRBeanArrayDataSource ds = new JRBeanArrayDataSource(productos.toArray());

        InputStream logoStream = getClass().getResourceAsStream("/img/logoV2.jpeg");
        BufferedImage logo = ImageIO.read(logoStream);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cliente_nombre", clienteNombre);
        parametros.put("cliente_direccion", clienteDireccion);
        parametros.put("cliente_telefono", clienteTelefono);

        java.sql.Date fechaSql = java.sql.Date.valueOf(fechaVenta.substring(0, 10));
        parametros.put("fecha_emision", fechaSql);
        parametros.put("fecha_creacion", fechaSql);
        parametros.put("forma_pago", "Contado");
        parametros.put("numero_factura", "FV-" + numeroFactura);
        parametros.put("logo", logo);

        double total = totalVenta;
        double subtotal = total;


        // Redondear a 2 decimales
        subtotal = Math.round(subtotal * 100.0) / 100.0;
        total = Math.round(total * 100.0) / 100.0;

        parametros.put("subtotal", subtotal);
        parametros.put("total", total);
        parametros.put("monto_en_letras", Metodos.NumeroEnLetras.convertir(total));
        parametros.put("ds", ds);

        // Exportar PDF
        JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource());
        String archivo = "Factura_" + numeroFactura + ".pdf";
        JasperExportManager.exportReportToPdfFile(jasperPrint, archivo);

        return new File(archivo);
    }


    public void enviarCorreoConAdjunto(String destinatario, String asunto, String cuerpo, File adjunto) throws Exception {

        final String remitente = "eclipsepitalitoalmacen@gmail.com";
        final String clave = "qagxhpujlokpgmcq"; // Usa contraseña de aplicación si es Gmail

        // Validación básica del correo
        if (destinatario == null || destinatario.isBlank() || !esCorreoValido(destinatario)) {
            throw new IllegalArgumentException("Correo inválido: " + destinatario);
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);

            // Parte del cuerpo
            MimeBodyPart cuerpoParte = new MimeBodyPart();
            cuerpoParte.setText(cuerpo);

            // Parte del adjunto
            MimeBodyPart adjuntoParte = new MimeBodyPart();
            adjuntoParte.attachFile(adjunto);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpoParte);
            multipart.addBodyPart(adjuntoParte);

            mensaje.setContent(multipart);

            Transport.send(mensaje);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }
    private boolean esCorreoValido(String correo) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return correo.matches(regex);
    }


    @FXML
    void GenerarExcel(ActionEvent event) {
        ObservableList<Factura> facturas = tablaProductos.getItems();

        if (facturas.isEmpty()) {
            org.controlsfx.control.Notifications.create()
                    .title("Aviso")
                    .text("No hay facturas para exportar.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Facturas");

        String[] headers = {
                "Número", "Cliente", "Correo", "Fecha Venta", "Subtotal", "Total", "Vendedor"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Factura f : facturas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(f.getNumeroFactura());
            row.createCell(1).setCellValue(f.getNombreCliente());
            row.createCell(2).setCellValue(f.getCorreo());
            row.createCell(3).setCellValue(f.getFechaDeVenta());
            row.createCell(4).setCellValue(f.getSubTotal());
            row.createCell(6).setCellValue(f.getTotal());
            row.createCell(7).setCellValue(f.getVendedor());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(root.getScene().getWindow());

            if (file != null) {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();

                org.controlsfx.control.Notifications.create()
                        .title("Éxito")
                        .text("Archivo Excel generado correctamente.")
                        .position(Pos.CENTER)
                        .showInformation();
            }

        } catch (IOException e) {
            org.controlsfx.control.Notifications.create()
                    .title("Error")
                    .text("Error al generar el archivo Excel: " + e.getMessage())
                    .position(Pos.CENTER)
                    .showError();
            e.printStackTrace();
        }
    }

    @FXML
    void cuadreCaja(ActionEvent event) {
        Factura facturaSeleccionada = tablaProductos.getSelectionModel().getSelectedItem();

        if (facturaSeleccionada == null) {
            Notifications.create()
                    .title("Información")
                    .text("Por favor selecciona una factura para tomar la fecha.")
                    .position(Pos.CENTER)
                    .showInformation();
            return;
        }

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
                .optionalEnd()
                .toFormatter();

        // Parsear fecha seleccionada
        LocalDate fechaSeleccionada = LocalDateTime.parse(facturaSeleccionada.getFechaDeVenta(), formatter).toLocalDate();

        double totalDia = 0.0;
        for (Factura f : tablaProductos.getItems()) {
            LocalDate fechaFactura = LocalDateTime.parse(f.getFechaDeVenta(), formatter).toLocalDate();
            if (fechaFactura.equals(fechaSeleccionada)) {
                totalDia += f.getTotal();
            }
        }

        Notifications.create()
                .title("Cuadre de Caja")
                .text("Total del día " + fechaSeleccionada + ": $" + String.format("%,.2f", totalDia))
                .position(Pos.CENTER)
                .showInformation();
    }

}

