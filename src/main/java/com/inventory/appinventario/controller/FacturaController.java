package com.inventory.appinventario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FacturaController  implements Initializable {

    @FXML
    private Button btnListar;

    @FXML
    private Button btnVerFacturas;

    @FXML
    private TextField cjBuscar;

    @FXML
    private TableColumn<?, ?> colCliente;

    @FXML
    private TableColumn<?, ?> colCodigo;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colTotal;

    @FXML
    private TableColumn<?, ?> colVendedor;

    @FXML
    private TableColumn<?, ?> coolFechaRegistro;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<?> tablaProductos;

    @FXML
    void buscarProductoKeyReleased(KeyEvent event) {

    }

    @FXML
    void listarFacturas(ActionEvent event) {

    }

    @FXML
    void verFacturas(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

