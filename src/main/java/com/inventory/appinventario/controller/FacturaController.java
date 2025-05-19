package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.FacturaDAO;
import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.DetalleFactura;
import com.inventory.appinventario.model.Factura;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
//import java.net.Authenticator;
//import java.net.PasswordAuthentication;
import java.net.URL;
import java.sql.*;
import java.util.*;

//para que los valores se vean en peso colombiano
import java.text.NumberFormat;

import javafx.scene.control.TableCell;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;


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
    private TableColumn<Factura, Number> colIva;
    @FXML
    private TableColumn<Factura, Number> colSubTotal;
    @FXML
    private TableColumn<Factura, String> colVendedor;

    private ConexionBD conexionBD;

    @FXML
    private AnchorPane root;


    @FXML
    void buscarProductoKeyReleased(KeyEvent event) {

    }

    @FXML
    void listarFacturas(ActionEvent event) {

    }

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
        colIva.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getIva())
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

        InputStream logoStream = getClass().getResourceAsStream("/img/logo.jpeg");
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
        double subtotal = total / 1.19;
        double iva = total - subtotal;

        // Redondear a 2 decimales
        subtotal = Math.round(subtotal * 100.0) / 100.0;
        iva = Math.round(iva * 100.0) / 100.0;
        total = Math.round(total * 100.0) / 100.0;

        parametros.put("subtotal", subtotal);
        parametros.put("iva", iva);
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

        final String remitente = "cris19970204@gmail.com";
        final String clave = "yrosdaopoqgvoxrh"; // Usa contraseña de aplicación si es Gmail

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

        // Contenedor
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(cuerpoParte);
        multipart.addBodyPart(adjuntoParte);

        mensaje.setContent(multipart);

        Transport.send(mensaje);
    }



    @FXML
    void GenerarExcel(ActionEvent event) {

    }

    @FXML
    void GenerarPdf(ActionEvent event) {

    }
}

