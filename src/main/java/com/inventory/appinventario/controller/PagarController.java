package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.VentaDAO;
import com.inventory.appinventario.model.Comercio;
import com.inventory.appinventario.model.DetalleVenta;
import com.inventory.appinventario.model.Venta;
import com.inventory.appinventario.util.ItemFacturaDTO;
import com.inventory.appinventario.util.Metodos;
import com.inventory.appinventario.util.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.view.JasperViewer;
import org.postgresql.util.PSQLException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagarController implements Initializable {

    private Venta venta;

    private int idventa=0;

    private double totalaPagar = 0;

    private double totalaPagariva = 0;

    private NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private ConexionBD conexionBD = new ConexionBD();

    private Metodos metodo=new Metodos();
    private VentaDAO ventaDAO;

    private Integer iva= Comercio.getInstance(null).getIva();

    @FXML
    private TextField cjValorIngreso;

    @FXML
    private VBox root;

    @FXML
    private Text txtCambio;

    @FXML
    private Text txtTotalAPagar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void cjValorIngresoKeyPressed(KeyEvent event) throws ParseException {

        if (event.getCode() == KeyCode.ENTER) {

            if (getValor() < totalaPagariva) {
                org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese un valor mayor o igual al total a pagar").position(Pos.CENTER).showWarning();
                return;
            }

            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿CONTINUAR CON EL REGISTRO?", ButtonType.YES, ButtonType.NO);
            a.setHeaderText("Header");
            if (a.showAndWait().get() == ButtonType.YES) {
                conexionBD.conectar();
                this.ventaDAO = new VentaDAO(conexionBD);
                try {
                    if ( (idventa=ventaDAO.guardar(this.venta)) > 0 ) {
                        com.inventory.appinventario.util.Metodos.closeEffect(root);

                        System.out.println("Intentando generar el reporte...");
                        // Cargar el archivo .jrxml o .jasper
                        JasperReport jr = JasperCompileManager.compileReport(getClass().getResourceAsStream("/reports/FacturaEclipse.jrxml"));

                        List<ItemFacturaDTO> datosTabla = new ArrayList<>();

                        for (DetalleVenta d : venta.getDetalleventa()) {
                            datosTabla.add(new ItemFacturaDTO(
                                    d.getCantidad(),
                                    d.getProducto().getNombreproducto(),
                                    d.getProducto().getTalla(),
                                    d.getPrecioventa(),
                                    d.getTotal()
                            ));
                        }
                        System.out.println("Contenido de datosTabla:");
                        for (ItemFacturaDTO item : datosTabla) {
                            System.out.println(item.getCantidad() + ", " + item.getDescripcion() + ", " + item.getTalla() + ", " + item.getPrecioUnitario() + ", " + item.getTotal());
                        }

                        // ⚠️ Solo ahora conviértelo en array
                        ItemFacturaDTO[] arrayDatos = datosTabla.toArray(new ItemFacturaDTO[0]);

                        System.out.println("📦 El DataSource final contiene " + arrayDatos.length + " elementos.");

                        // ⚠️ Y solo ahora crea el datasource
                        JRBeanArrayDataSource ds = new JRBeanArrayDataSource(arrayDatos);



                        InputStream logoStream = getClass().getResourceAsStream("/img/logo.jpeg"); // si está dentro de src/main/resources/images
                        BufferedImage logo = ImageIO.read(logoStream);




                        System.out.println("cargando......");
                        // Parámetros
                        Map<String, Object> parametros = new HashMap<>();
                        parametros.put("cliente_nombre", venta.getCliente().getNombrecliente());
                        parametros.put("cliente_direccion", venta.getCliente().getDireccioncliente());
                        parametros.put("cliente_telefono", venta.getCliente().getTelefonocliente());
                        parametros.put("fecha_emision", new java.sql.Date(System.currentTimeMillis()));
                        parametros.put("fecha_creacion", new java.sql.Date(System.currentTimeMillis()));
                        parametros.put("forma_pago", venta.getFormadepago());
                        parametros.put("numero_factura", "FV-" + idventa);
                        parametros.put("subtotal", venta.getSubtotal());
                        parametros.put("iva", venta.getIva());
                        parametros.put("total", venta.getTotal());
                        parametros.put("monto_en_letras", Metodos.NumeroEnLetras.convertir(venta.getTotal()));
                        parametros.put("logo", logo);
                        parametros.put("ds", ds);
                        System.out.println("carga datos");


                        // Llenar el reporte
                        JasperPrint jp = JasperFillManager.fillReport(jr, parametros, new JREmptyDataSource());                        System.out.println("Reporte llenado."); // <-- Agrega esto
                        // Mostrarlo
                        JasperViewer viewer = new JasperViewer(jp, false);
                        viewer.setTitle("Factura Eclipse");
                        viewer.setVisible(true);
                        System.out.println("Visor del reporte mostrado.");
                    }
                } catch (PSQLException ex) {
                    //Logger.getLogger(RegistrarVentaController.class.getName()).log(Level.SEVERE, null, ex);
                    org.controlsfx.control.Notifications.create().title("Aviso").text(ex.getServerErrorMessage().getMessage()).position(Pos.CENTER).showError();
                } catch (SQLException ex) {
                    //Logger.getLogger(RegistrarVentaController.class.getName()).log(Level.SEVERE, null, ex);
                    org.controlsfx.control.Notifications.create().title("Aviso").text("NO SE PUDO REGISTRAR LA VENTA \n" + ex.getMessage()).position(Pos.CENTER).showError();
                } catch (Exception ex) {
                    //Logger.getLogger(RegistrarVentaController.class.getName()).log(Level.SEVERE, null, ex);
                    org.controlsfx.control.Notifications.create().title("Aviso").text("ERROR INESPERADO\n" + ex.getMessage()).position(Pos.CENTER).showError();
                }
            }
        }

    }
    @FXML
    private void cjValorIngresokeyReleased(KeyEvent event) {
        double pagar = getValor();
        txtCambio.setText(format.format((pagar - this.totalaPagariva)));
    }

    public double getValor() {
        try {
            String texto = cjValorIngreso.getText().trim();

            // Elimina símbolos de moneda, espacios, puntos y comas, dejando solo números y el punto decimal
            texto = texto.replaceAll("[^\\d.,]", "");

            // Reemplaza comas por puntos si tu formato decimal los usa
            if (texto.contains(",") && !texto.contains(".")) {
                texto = texto.replace(",", ".");
            }

            return Double.parseDouble(texto);
        } catch (Exception ex) {
            Logger.getLogger(PagarController.class.getName()).log(Level.SEVERE, null, ex);
            return 0.0;
        }
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
        totalaPagar = this.venta.getDetalleventa().stream().mapToDouble(ped -> ped.getCantidad() * ped.getPrecioventa()).sum();

        totalaPagariva=totalaPagar+((totalaPagar*this.iva)/100);

        txtTotalAPagar.setText(NumberFormat.getCurrencyInstance().format(totalaPagariva));
    }

    public int getIdventa() {
        return idventa;
    }
}