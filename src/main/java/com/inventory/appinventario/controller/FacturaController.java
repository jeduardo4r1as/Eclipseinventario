package com.inventory.appinventario.controller;

        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.TableColumn;
        import javafx.scene.control.TableView;
        import javafx.scene.control.TextField;
        import javafx.scene.input.KeyEvent;
        import javafx.scene.layout.AnchorPane;

public class FacturaController {

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnListar;

    @FXML
    private TextField cjBuscar;

    @FXML
    private TableColumn<?, ?> colCodigo;

    @FXML
    private TableColumn<?, ?> colDescripcion;

    @FXML
    private TableColumn<?, ?> colEstado;

    @FXML
    private TableColumn<?, ?> colGenero;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colPrecioMayorista;

    @FXML
    private TableColumn<?, ?> colReferencia;

    @FXML
    private TableColumn<?, ?> colTalla;

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
    void editarProducto(ActionEvent event) {

    }

    @FXML
    void listarProductos(ActionEvent event) {

    }

}
