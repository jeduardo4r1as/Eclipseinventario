package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.FacturaDAO;
import com.inventory.appinventario.model.Factura;
import com.inventory.appinventario.util.ConexionBD;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

//para que los valores se vean en peso colombiano
import java.text.NumberFormat;
import java.util.Locale;
import javafx.scene.control.TableCell;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

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
    private TableColumn<Factura, Number> colTotal;
    @FXML
    private TableColumn<Factura, String> colFechaVenta;
    @FXML
    private TableColumn<Factura, Number> colIva;
    @FXML
    private TableColumn<Factura, Number> colSubTotal;
    @FXML
    private TableColumn<Factura, String> colVendedor;

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
            System.out.println("⚠️ Debes seleccionar una factura");
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
    void GenerarExcel(ActionEvent event) {

    }

    @FXML
    void GenerarPdf(ActionEvent event) {

    }
}

