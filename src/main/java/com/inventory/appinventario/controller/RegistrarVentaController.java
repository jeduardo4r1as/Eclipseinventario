package com.inventory.appinventario.controller;

import animatefx.animation.Tada;
import com.inventory.appinventario.dao.ClienteDAO;
import com.inventory.appinventario.dao.ProductoDAO;
import com.inventory.appinventario.model.*;
import com.inventory.appinventario.util.ConexionBD;
import com.inventory.appinventario.util.CurrencyCell;
import com.inventory.appinventario.util.DoubleCell;
import com.inventory.appinventario.util.SearchComboBox;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrarVentaController implements Initializable {

    @FXML
    private Button btnAgregarProducto;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnAgregarCliente;

    @FXML
    private Button btnPagar;

    @FXML
    private Button btnQuitarProducto;

    @FXML
    private TextField cjBuscarProducto;

    @FXML
    private TextField cjCodigoBarras;

    @FXML
    private DatePicker cjFecha;

    @FXML
    private TableColumn<DetalleVenta, String> colProducto;


    @FXML
    private TableColumn colcantidad;

    @FXML
    private TableColumn<DetalleVenta, Double> coltotal;

    @FXML
    private TableColumn colvalor;

    @FXML
    private ComboBox<String> comboFormaDePago;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label lblIva;

    @FXML
    private VBox root;

    @FXML
    private TableView<DetalleVenta> tablaPedidos;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colProductos;

    @FXML
    private TableColumn<Producto, String> colTalla;

    @FXML
    private TableColumn<Producto, String> colGenero;

    @FXML
    private TableColumn<Producto, Double> colProductoPrecio;

    @FXML
    private Text txtIva;

    @FXML
    private Text txtSubtotal;

    @FXML
    private Text txtTituloEmpresa;

    @FXML
    private Text txtTotal;

    @FXML
    private ComboBox<String> comboTiposDePrecio;

    @FXML
    private TableColumn colTipoPrecioVenta;

    private Stage stageCliente;

    private RegistrarClienteController registrarclienteController;

    SearchComboBox<Cliente> comboCliente = new SearchComboBox<>();

    private ConexionBD conexionBD = new ConexionBD();
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;

    FilteredList<Producto> filtro;
    ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    ObservableList<DetalleVenta> listaPedido = FXCollections.observableArrayList();

    private Integer iva= Comercio.getInstance(null).getIva();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnPagar.disableProperty().bind(Bindings.isEmpty(listaPedido));

        cjFecha.setValue(LocalDate.now());


        cjCodigoBarras.requestFocus();
        comboFormaDePago.getItems().addAll("EFECTIVO", "TARJETA CREDITO", "TARJETA DEBITO");
        comboFormaDePago.getSelectionModel().selectFirst();

        comboTiposDePrecio.getItems().addAll("UNITARIO", "MAYORISTA", "DISTRIBUIDOR");
        comboTiposDePrecio.getSelectionModel().selectFirst();

        tablaPedidos.setEditable(true);
        tablaPedidos.getSelectionModel().setCellSelectionEnabled(true);
        tablaPedidos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tablaPedidos.setItems(listaPedido);
        tablaPedidos.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);


        colProducto.setCellValueFactory(tc -> tc.getValue().getProducto().nombreproductoProperty());

        colcantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colcantidad.setStyle("-fx-alignment: CENTER;");
        colcantidad.setCellFactory(tc -> new DoubleCell<>());
        colcantidad.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DetalleVenta, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<DetalleVenta, Double> e) {
                if (!Objects.equals(e.getNewValue(), e.getOldValue())) {
                    ((DetalleVenta) e.getTableView().getItems().get(e.getTablePosition().getRow())).setCantidad(e.getNewValue());
                    calcular();
                    com.inventory.appinventario.util.Metodos.changeSizeOnColumn(coltotal, tablaPedidos, e.getTablePosition().getRow());
                }
            }
        });

        colTipoPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("tipoPrecio"));

        colvalor.setCellValueFactory(new PropertyValueFactory<>("precioventa"));
        colvalor.setCellFactory(tc -> new CurrencyCell<>());
        colvalor.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<DetalleVenta, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<DetalleVenta, Double> e) {
                if (!Objects.equals(e.getNewValue(), e.getOldValue())) {
                    ((DetalleVenta) e.getTableView().getItems().get(e.getTablePosition().getRow())).setPrecioventa(e.getNewValue());
                    calcular();
                    com.inventory.appinventario.util.Metodos.changeSizeOnColumn(colvalor, tablaPedidos, e.getTablePosition().getRow());
                }
            }
        });

        coltotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        coltotal.setCellFactory(tc -> new CurrencyCell<>());
        coltotal.setEditable(false);


        try {
            conexionBD.conectar();
            clienteDAO = new ClienteDAO(conexionBD);

            ObservableList<Cliente> clientes = FXCollections.observableArrayList(clienteDAO.getAll());

            comboCliente.setItems(clientes); // USANDO el campo global correctamente

            comboCliente.selectedItemProperty().addListener((obs, oldVal, nuevo) -> {
                System.out.println("Cliente seleccionado: " + nuevo);
            });

            gridPane.add(comboCliente, 0, 1);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            this.conexionBD.CERRAR();
        }

        try {
            this.conexionBD.conectar();
            productoDAO = new ProductoDAO(this.conexionBD);
            listaProductos.addAll(productoDAO.getAll());
            tablaProductos.setItems(listaProductos);
            filtro = new FilteredList(listaProductos, p -> true);
            colProductos.setCellValueFactory(param -> param.getValue().nombreproductoProperty());
            colProductoPrecio.setCellValueFactory(new PropertyValueFactory<>("preciounitario"));
            colGenero.setCellValueFactory(param -> param.getValue().generoProperty());
            colTalla.setCellValueFactory(param -> param.getValue().tallaProperty());

        } catch (SQLException ex) {
            org.controlsfx.control.Notifications.create().title("Aviso").text("No se cargaron los productos").position(Pos.CENTER).showWarning();
            Logger.getLogger(RegistrarVentaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        txtTituloEmpresa.setText(Comercio.getInstance(null).getNombre());
        lblIva.setText("IVA: ("+this.iva+"%)");

        // Enlazar el filtro una sola vez aquí
        filtro = new FilteredList<>(listaProductos, p -> true);
        SortedList<Producto> sorterData = new SortedList<>(filtro);
        sorterData.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(sorterData);

        }



    @FXML
    void agregarcliente(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/RegistrarCliente.fxml"));
        AnchorPane ap = loader.load();
        registrarclienteController = loader.getController();
        Scene scene = new Scene(ap);
        stageCliente = new Stage();
        stageCliente.setScene(scene);
        stageCliente.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/add_user.png")));
        stageCliente.initOwner(root.getScene().getWindow());
        stageCliente.initModality(Modality.WINDOW_MODAL);
        stageCliente.initStyle(StageStyle.DECORATED);
        stageCliente.setResizable(false);
        stageCliente.setOnCloseRequest((WindowEvent e) -> {
            root.setEffect(null);
        });
        stageCliente.setOnHidden((WindowEvent e) -> {
            root.setEffect(null);
        });
        root.setEffect(new GaussianBlur(7.0));
        stageCliente.showAndWait();

        carcagarclientes();

    }

    @FXML
    private void buscarCodigo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && cjCodigoBarras.getText() != null && !cjCodigoBarras.getText().isEmpty()) {

            String codigo = cjCodigoBarras.getText();
            String tipoPrecio = comboTiposDePrecio.getValue();

            listaPedido.stream()
                    .filter(p -> p.getProducto().getCodigodebarras().equals(codigo)
                            && p.getTipoPrecio().equals(tipoPrecio))
                    .findFirst().map((t) -> {
                        t.setCantidad(t.getCantidad() + 1);
                        cjCodigoBarras.setText(null);
                        return t;
                    }).orElseGet(() -> {
                        listaProductos.stream()
                                .filter((p) -> p.getCodigodebarras().equals(codigo))
                                .findFirst()
                                .ifPresent(p -> {
                                    DetalleVenta dv = new DetalleVenta();
                                    dv.setProducto(p);
                                    dv.setCantidad(1);
                                    dv.setTipoPrecio(tipoPrecio);

                                    double precioSeleccionado;
                                    switch (tipoPrecio) {
                                        case "MAYORISTA":
                                            precioSeleccionado = p.getPreciomayorista();
                                            break;
                                        case "DISTRIBUIDOR":
                                            precioSeleccionado = p.getPreciodistribuidor();
                                            break;
                                        default:
                                            precioSeleccionado = p.getPreciounitario();
                                    }

                                    dv.setPrecioventa(precioSeleccionado);
                                    listaPedido.add(dv);
                                    com.inventory.appinventario.util.Metodos.changeSizeOnColumn(colProducto, tablaPedidos, -1);
                                    cjCodigoBarras.setText(null);
                                });
                        return null;
                    });

            calcular();

        } else if (event.getCode() == KeyCode.DOWN) {
            tablaPedidos.requestFocus();
            tablaPedidos.getFocusModel().focus(0, colcantidad);
            tablaPedidos.getSelectionModel().select(0, colcantidad);
        } else if (event.getCode() == KeyCode.RIGHT && cjCodigoBarras.getText() == null) {
            cjBuscarProducto.requestFocus();
        }
    }

    @FXML
    private void buscarProducto(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ENTER || evt.getCode() == KeyCode.UP || evt.getCode() == KeyCode.DOWN) {
            return;
        }

        String newValue = cjBuscarProducto.getText().toUpperCase();

        filtro.setPredicate(producto -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            return producto.getNombreproducto().toUpperCase().contains(newValue);
        });

        switch (evt.getCode()) {
            case DOWN:
                tablaProductos.requestFocus();
                tablaProductos.getSelectionModel().select(0, colProductos);
                break;
            case ESCAPE:
                cjCodigoBarras.requestFocus();
                break;
        }
    }

    @FXML
    void cancelarPedido(ActionEvent event) {

            comboCliente.clear();
            listaPedido.clear();
            cjCodigoBarras.requestFocus();
            txtSubtotal.setText("$0");
            txtIva.setText("$0");
            txtTotal.setText("$0");

    }

    @FXML
    private void pagar(ActionEvent event) throws IOException {

        if(comboCliente.getSelectedItem()==null){

            new Tada(comboCliente).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Seleccione un cliente").position(Pos.CENTER).showWarning();
            return;
        }

        Venta v = new Venta();
        v.setCliente(comboCliente.getSelectedItem());
        v.setFormadepago(comboFormaDePago.getSelectionModel().getSelectedItem());
        v.setDetalleventa(listaPedido);
        double suma = listaPedido.stream().mapToDouble(ped -> ped.getCantidad() * ped.getPrecioventa()).sum();
        double iva = (suma*this.iva)/100.0;
        double total=Math.round((suma+iva) * 100.0) / 100.0;
        v.setSubtotal(suma);
        v.setTotal(total);
        v.setIva(iva);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/Pagar.fxml"));
        VBox vbox = loader.load();

        PagarController controller = loader.getController();
        controller.setVenta(v);

        Scene scene = new Scene(vbox);
        Stage stage = new Stage();
        stage.setTitle("Confirmar venta");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/payment.png")));
        stage.setScene(scene);
        stage.initOwner(root.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setIconified(false);
        stage.showAndWait();

        if(controller.getIdventa() > 0){
            comboCliente.clear();
            listaPedido.clear();
            cjCodigoBarras.requestFocus();
            txtSubtotal.setText("$0");
            txtIva.setText("$0");
            txtTotal.setText("$0");
        }
    }

    @FXML
    void restarCantidad(ActionEvent event) {
        listaPedido.removeAll(tablaPedidos.getSelectionModel().getSelectedItems().filtered(p -> p.getCantidad() == 1));
        tablaPedidos.getSelectionModel().getSelectedItems().stream().filter(p -> p.getCantidad() > 1).forEach(p -> {
            p.setCantidad((p.getCantidad() - 1));
        });
        calcular();
    }

    @FXML
    void sumarCantidad(ActionEvent event) {
        tablaPedidos.getSelectionModel().getSelectedItems().stream().forEach(p -> {
            p.setCantidad((p.getCantidad() + 1));
            calcular();
        });
    }

    @FXML
    void tablaPedidosKeyPressed(KeyEvent evt) {
        if (evt.getCode().isDigitKey()) {
            final TablePosition focusedCell = tablaPedidos.focusModelProperty().get().focusedCellProperty().get();
            tablaPedidos.edit(focusedCell.getRow(), focusedCell.getTableColumn());
        } else if (evt.getCode() == KeyCode.ESCAPE) {
            cjCodigoBarras.requestFocus();
            tablaPedidos.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void tablaProductosKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            cjBuscarProducto.requestFocus();
            tablaProductos.getSelectionModel().clearSelection();
        } else if (event.getCode() == KeyCode.ENTER) {
          agregarPedido(tablaProductos.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void tablaProductosMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Producto pr = tablaProductos.getSelectionModel().getSelectedItem();
            agregarPedido(pr);
        }
    }

    void agregarPedido(Producto pr) {
        double precioSeleccionado;

        // Escoger el precio de acuerdo a la opción seleccionada en el ComboBox
        String tipoPrecio = comboTiposDePrecio.getValue();
        if ("MAYORISTA".equals(tipoPrecio)) {
            precioSeleccionado = pr.getPreciomayorista();
        } else if ("DISTRIBUIDOR".equals(tipoPrecio)) {
            precioSeleccionado = pr.getPreciodistribuidor();
        } else {
            precioSeleccionado = pr.getPreciounitario(); // Por defecto, Unitario
        }

        listaPedido.stream()

                .filter(p -> p.getProducto().getIdproducto() == pr.getIdproducto()
                        && p.getTipoPrecio().equals(tipoPrecio))
                .findFirst().map((t) -> {
                    t.setCantidad(t.getCantidad() + 1);
                    com.inventory.appinventario.util.Metodos.changeSizeOnColumn(coltotal, tablaPedidos, -1);
                    cjCodigoBarras.setText(null);
                    return t;
                }).orElseGet(() -> {
                    DetalleVenta dv = new DetalleVenta();
                    dv.setProducto(pr);
                    dv.setTipoPrecio(comboTiposDePrecio.getValue());
                    dv.setCantidad(1);
                    dv.setPrecioventa(precioSeleccionado); // Aquí se usa el precio correcto
                    listaPedido.add(dv);
                    com.inventory.appinventario.util.Metodos.changeSizeOnColumn(colProductos, tablaPedidos, -1);
                    cjCodigoBarras.setText(null);
                    return dv;
                });

        calcular();
    }


    private void calcular() {
        double suma = listaPedido.stream().mapToDouble(ped -> ped.getCantidad() * ped.getPrecioventa()).sum();
        double iva = (suma*this.iva)/100.0;
        txtIva.setText(NumberFormat.getCurrencyInstance().format(iva));
        txtSubtotal.setText(NumberFormat.getCurrencyInstance().format((suma)));
        txtTotal.setText(NumberFormat.getCurrencyInstance().format((suma+iva)));
    }


    //funcion que actualiza los precios de la tabla de acuerdo a la opcion que selecciones crm
    @FXML
    private void funcionTipoPrecio(ActionEvent event) {
        String tipoPrecio = comboTiposDePrecio.getSelectionModel().getSelectedItem();

        try {
            this.conexionBD.conectar();
            productoDAO = new ProductoDAO(this.conexionBD);

            // Actualiza el contenido de la lista sin destruir filtro
            List<Producto> productos = productoDAO.getAll();
            listaProductos.setAll(productos); // OK: mantiene el mismo objeto observable

            // Cambia la columna visible según el tipo de precio
            switch (tipoPrecio) {
                case "UNITARIO":
                    colProductoPrecio.setCellValueFactory(new PropertyValueFactory<>("preciounitario"));
                    break;
                case "MAYORISTA":
                    colProductoPrecio.setCellValueFactory(new PropertyValueFactory<>("preciomayorista"));
                    break;
                case "DISTRIBUIDOR":
                    colProductoPrecio.setCellValueFactory(new PropertyValueFactory<>("preciodistribuidor"));
                    break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carcagarclientes(){
        try {
            conexionBD.conectar();
            clienteDAO = new ClienteDAO(conexionBD);

            ObservableList<Cliente> clientes = FXCollections.observableArrayList(clienteDAO.getAll());

            comboCliente.setItems(clientes); // USANDO el campo global correctamente

            comboCliente.selectedItemProperty().addListener((obs, oldVal, nuevo) -> {
                System.out.println("Cliente seleccionado: " + nuevo);
            });

            gridPane.add(comboCliente, 0, 1);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            this.conexionBD.CERRAR();
        }
    }


}
