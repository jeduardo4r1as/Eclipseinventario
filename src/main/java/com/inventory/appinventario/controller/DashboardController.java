package com.inventory.appinventario.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import com.jfoenix.controls.JFXTabPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private BorderPane root;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu menuArchivo;

    @FXML
    private MenuItem menuconfig;

    @FXML
    private MenuItem menuSalir;

    @FXML
    private Menu menuProductos;

    @FXML
    private MenuItem menuVerProductos;

    @FXML
    private MenuItem menuVerFacturas;

    @FXML
    private Menu menuVentas;

    @FXML
    private MenuItem menuRealizarVenta;

    @FXML
    private Menu menuClientes;

    @FXML
    private MenuItem nuevoCliente;

    @FXML
    private TabPane tabPane;

    private Tab tabProductos;

    private Tab tabUsuarios;

    private Tab tabFacturas;

    private Tab tabEstadistica;

    private Tab tabClientes;


    @FXML
    void abrirConfiguracion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/Configuracion.fxml")); // Ajusta la ruta
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Configuración");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL); //
        stage.showAndWait();
    }

    @FXML
    void mostraRealizarVenta(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/RegistrarVenta.fxml"));
        VBox ap = loader.load();
        RegistrarVentaController rvc = loader.getController();
        Tab tabRealizarVenta = new Tab(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE dd MMM hh:mm:ss a")), ap);
        tabRealizarVenta.setClosable(true);
        tabPane.getTabs().add(tabRealizarVenta);
//        rvc.getCjCodigoBarras().requestFocus();
        tabPane.getSelectionModel().select(tabRealizarVenta);
    }


    @FXML
    void mostrarTablaProductos(ActionEvent event) throws IOException {
        if (tabProductos == null) {
            AnchorPane ap = FXMLLoader.load(getClass().getResource("/com/inventory/appinventario/Producto.fxml"));
            tabProductos = new Tab("PRODUCTOS", ap);
            tabProductos.setClosable(true);
            tabProductos.setOnClosed(event1 -> {
                tabProductos = null;
            });
            tabPane.getTabs().add(tabProductos);
        }
        tabPane.getSelectionModel().select(tabProductos);

    }

    @FXML
    void mostrarTablaCliente(ActionEvent event) throws IOException {
        if (tabClientes == null) {
            AnchorPane ap = FXMLLoader.load(getClass().getResource("/com/inventory/appinventario/Cliente.fxml"));
            tabClientes = new Tab("CLIENTES", ap);
            tabClientes.setClosable(true);
            tabClientes.setOnClosed(event1 -> {
                tabClientes = null;
            });
            tabPane.getTabs().add(tabClientes);
        }
        tabPane.getSelectionModel().select(tabClientes);


    }

    @FXML
    void mostrarTablaUsuarios(ActionEvent event) throws IOException {
        if (tabUsuarios == null) {
            AnchorPane ap = FXMLLoader.load(getClass().getResource("/com/inventory/appinventario/Usuario.fxml"));
            tabUsuarios = new Tab("USUARIOS", ap);
            tabUsuarios.setClosable(true);
            tabUsuarios.setOnClosed(event1 -> {
                tabUsuarios = null;
            });
            tabPane.getTabs().add(tabUsuarios);
        }
        tabPane.getSelectionModel().select(tabUsuarios);


    }

    @FXML
    void mostrarFacturas(ActionEvent event) throws IOException {
        if (tabFacturas == null) {
            AnchorPane ap = FXMLLoader.load(getClass().getResource("/com/inventory/appinventario/Factura.fxml"));
            tabFacturas = new Tab("FACTURAS", ap);
            tabFacturas.setClosable(true);
            tabFacturas.setOnClosed(event1 -> {
                tabFacturas = null;
            });
            tabPane.getTabs().add(tabFacturas);
        }
        tabPane.getSelectionModel().select(tabFacturas);


    }


    @FXML
    void mostrarEstadisticas(ActionEvent event) throws IOException {
        if (tabEstadistica == null) {
            AnchorPane ap = FXMLLoader.load(getClass().getResource("/com/inventory/appinventario/Estadisticas.fxml"));
            tabEstadistica = new Tab("ESTADISTICAS", ap);
            tabEstadistica.setClosable(true);
            tabEstadistica.setOnClosed(event1 -> {
                tabEstadistica = null;
            });
            tabPane.getTabs().add(tabEstadistica);
        }
        tabPane.getSelectionModel().select(tabEstadistica);


    }






    @FXML
    void salir(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de salida");
        alert.setHeaderText("¿Estás seguro que deseas salir?");
        alert.setContentText("Confirma si deseas cerrar la aplicación.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            Platform.exit();
        }
    }
}
