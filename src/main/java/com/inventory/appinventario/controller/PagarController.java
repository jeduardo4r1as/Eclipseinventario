package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.VentaDAO;
import com.inventory.appinventario.model.Venta;
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
import org.postgresql.util.PSQLException;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagarController implements Initializable {

    private Venta venta;

    private int idventa=0;

    private double totalaPagar = 0;

    private NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private ConexionBD conexionBD = new ConexionBD();
    private VentaDAO ventaDAO;

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

            if (getValor() < totalaPagar) {
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
                        //                      JasperReport jr = (JasperReport) JRLoader.loadObject(new URL(getClass().getResource("/reports/factura.jasper").toString()));
                        //                    Map<String, Object> parametros = new HashMap<>();
                        //                  parametros.put("idventa", idventa);
                        //                JasperPrint jasperprint = JasperFillManager.fillReport(jr, parametros, this.conexionBD.getConexion());
                        //              JasperViewer viewer = new JasperViewer(jasperprint, false);
                        //            viewer.setVisible(true);
                        //          viewer.toFront();
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
        txtCambio.setText(format.format((pagar - this.totalaPagar)));
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
        txtTotalAPagar.setText(NumberFormat.getCurrencyInstance().format(totalaPagar));
    }

    public int getIdventa() {
        return idventa;
    }
}