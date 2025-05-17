package com.inventory.appinventario.controller;

import com.inventory.appinventario.model.DetalleFactura;
import com.inventory.appinventario.util.ConexionBD;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class FacturaDetalleController {

    @FXML private Label idCliente;
    @FXML private Label idFecha;
    @FXML private Label idTotal;

    @FXML private TableView<DetalleFactura> tablaDetalle;
    @FXML private TableColumn<DetalleFactura, String> colProducto;
    @FXML private TableColumn<DetalleFactura, Number> colCantidad;
    @FXML private TableColumn<DetalleFactura, Number> colPrecio;
    @FXML private TableColumn<DetalleFactura, Number> colSubtotal;
    @FXML private TableColumn<DetalleFactura, String> colDescripcion;
    @FXML private TableColumn<DetalleFactura, String> colTalla;
    @FXML private TableColumn<DetalleFactura, String> colTipoPrecio;

    @FXML private Button btnDescargarFactura;

    private final ConexionBD conexionBD = new ConexionBD();
    private int numeroFacturaGlobal;

    public void cargarFacturaDetalle(int numeroFactura) {
        this.numeroFacturaGlobal = numeroFactura;
        conexionBD.conectar();
        ObservableList<DetalleFactura> detalles = FXCollections.observableArrayList();

        String sql = """
            SELECT c.nombrecliente, v.fechadeventa, v.total,
                   p.nombreproducto, p.descripcion, p.talla,
                   dv.cantidad, dv.preciodeventa, dv.tipoprecio
            FROM venta v
            INNER JOIN cliente c ON c.idcliente = v.idcliente
            INNER JOIN detalleventa dv ON dv.idventa = v.idventa
            INNER JOIN producto p ON p.idproducto = dv.idproducto
            WHERE v.numerofactura = ?
        """;

        try (PreparedStatement pst = conexionBD.getConexion().prepareStatement(sql)) {
            pst.setInt(1, numeroFactura);
            ResultSet rs = pst.executeQuery();

            boolean firstRow = true;

            while (rs.next()) {
                if (firstRow) {
                    idCliente.setText(rs.getString("nombrecliente"));
                    idFecha.setText(rs.getString("fechadeventa"));
                    idTotal.setText(String.valueOf(rs.getDouble("total")));
                    firstRow = false;
                }

                DetalleFactura detalle = new DetalleFactura(
                        rs.getString("nombreproducto"),
                        rs.getDouble("cantidad"),
                        rs.getDouble("preciodeventa"),
                        rs.getDouble("cantidad") * rs.getDouble("preciodeventa"),
                        rs.getString("tipoprecio"),
                        rs.getString("descripcion"),
                        rs.getString("talla")
                );

                detalles.add(detalle);
            }

            tablaDetalle.setItems(detalles);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        colProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProducto()));
        colCantidad.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCantidad()));
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPreciodeventa()));
        colSubtotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSubtotal()));
        colDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));
        colTalla.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTalla()));
        colTipoPrecio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoprecio()));
    }

    @FXML
    public void descargarPdf(javafx.event.ActionEvent actionEvent) {
        try {
            InputStream input = getClass().getResourceAsStream("/reports/FacturaEclipse.jrxml");
            if (input == null) throw new FileNotFoundException("No se encontró el archivo FacturaEclipse.jrxml");

            JasperReport reporte = JasperCompileManager.compileReport(input);

            // Parámetros del encabezado
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cliente_nombre", idCliente.getText());
            parametros.put("cliente_direccion", "Pitalito Huila");
            parametros.put("cliente_telefono", "3222844551");

            java.sql.Date fechaSql = java.sql.Date.valueOf(idFecha.getText().substring(0, 10));
            parametros.put("fecha_emision", fechaSql);
            parametros.put("fecha_creacion", fechaSql);
            parametros.put("forma_pago", "Contado");
            parametros.put("numero_factura", String.valueOf(numeroFacturaGlobal));

            // Cálculo monetario
            List<DetalleFactura> productos = tablaDetalle.getItems();
            double subtotal = productos.stream().mapToDouble(DetalleFactura::getSubtotal).sum();
            double iva = subtotal * 0.19;
            double total = subtotal + iva;

            parametros.put("subtotal", subtotal);
            parametros.put("iva", iva);
            parametros.put("total", total);
            parametros.put("monto_en_letras", "Son: " + total + " pesos"); // Reemplazar por utilidad si deseas convertir a texto

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(productos);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, dataSource);
            String archivo = "Factura_" + numeroFacturaGlobal + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, archivo);

            File pdf = new File(archivo);
            if (pdf.exists()) Desktop.getDesktop().open(pdf);

            System.out.println("Factura generada y abierta exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al generar el PDF.");
        }

      

    }

    public void cancelar(ActionEvent actionEvent) {

        Button boton = (Button) actionEvent.getSource();
        boton.getScene().getWindow().hide();


    }
}

