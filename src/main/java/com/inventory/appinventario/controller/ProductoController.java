package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.ProductoDAO;
import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.util.ConexionBD;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductoController implements Initializable {



    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnListar;

    @FXML
    private Button btnNuevo;

    @FXML
    private TextField cjBuscar;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colDescripcion;

    @FXML
    private TableColumn<Producto, String> colEstado;

    @FXML
    private TableColumn<Producto, String> colGenero;
    @FXML
    private TableColumn<Producto, String> colTalla;
    @FXML
    private TableColumn<Producto, Double> colPrecioMayorista;

    @FXML
    private TableColumn<Producto, Double> colPrecioUnitario;

    @FXML
    private TableColumn<Producto, Double> colPrecioDistribuidor;

    @FXML
    private TableColumn<Producto, String> colReferencia;

    @FXML
    private TableColumn<Producto, Double> colStock;

    @FXML
    private TableColumn<Producto,Double> colStockMinimo;

    @FXML
    private TableColumn<Producto, LocalDate> coolFechaRegistro;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Producto> tablaProductos;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    private ConexionBD conexionBD = new ConexionBD();
    private ProductoDAO productoDAO;

    private Stage stageProducto;
    private RegistrarProductoController registrarProductoController;

    private ObjectProperty<Producto> objProducto = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colNombre.setCellValueFactory(param -> param.getValue().nombreproductoProperty());

        colCodigo.setCellValueFactory(param -> param.getValue().codigodebarrasProperty());

        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colStock.setCellFactory(param -> {
          return new TableCell<Producto, Double>(){

              protected void updateItem(Double value,boolean empty){
                  super.updateItem(value, empty);
                  if (value == null || empty) {
                      setText(null);
                      setStyle(null);
                  } else {
                      setAlignment(Pos.CENTER);
                      setText("" +value);
                      Producto producto = getTableView().getItems().get(getIndex());
                      if(value > producto.getStockminimo()){
                          setStyle("-fx-background-color: #4CAF50; fx-font-weight: bold; -fx-text-fill: white; ");
                      }else if(value > 1 && value <= getTableView().getItems().get(getIndex()).getStockminimo() ){
                          setStyle("-fx-background-color: #FFFF00; fx-font-weight: bold; -fx-text-fill: black; ");
                      }else if(value < 1){
                          setStyle("-fx-background-color: #F44336; fx-font-weight: bold; -fx-text-fill: white; ");
                      }
                  }
              }
          };
        });


        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockminimo"));
        colTalla.setCellValueFactory(param -> param.getValue().tallaProperty());
        colDescripcion.setCellValueFactory(param -> param.getValue().descripcionProperty());
        colGenero.setCellValueFactory(param -> param.getValue().generoProperty());
        colReferencia.setCellValueFactory(param -> param.getValue().referenciaProperty());
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("preciounitario"));
        colPrecioMayorista.setCellValueFactory(new PropertyValueFactory<>("preciomayorista"));
        colPrecioDistribuidor.setCellValueFactory(new PropertyValueFactory<>("preciodistribuidor"));
        colEstado.setCellValueFactory(param -> param.getValue().estadoProperty());
        coolFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaderegistro"));

        tablaProductos.setItems(listaProductos);
        objProducto.bind(tablaProductos.getSelectionModel().selectedItemProperty());

        }
    @FXML
    void borrarProducto(ActionEvent event) throws IOException, SQLException {
        if(objProducto.get()==null){
            com.inventory.appinventario.util.Metodos.rotarError(tablaProductos);
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿DESEA ELIMINAR EL PRODUCTO?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(this.objProducto.get().getNombreproducto());
        if (a.showAndWait().get() == ButtonType.YES) {
            conexionBD.conectar();
            productoDAO = new ProductoDAO(conexionBD);
            boolean delete = productoDAO.delete(objProducto.get().getIdproducto());
            listarProductos(null);
            this.conexionBD.CERRAR();
        }

    }

    @FXML
    private void ActivarProducto(ActionEvent event) throws IOException, SQLException{
        if(objProducto.get()==null){
            com.inventory.appinventario.util.Metodos.rotarError(tablaProductos);
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿DESEA ACTIVAR EL USUARIO?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(this.objProducto.get().getNombreproducto());
        if (a.showAndWait().get() == ButtonType.YES) {
            conexionBD.conectar();
            productoDAO = new ProductoDAO(conexionBD);
            boolean delete = productoDAO.activar(objProducto.get().getIdproducto());
            listarProductos(null);
            this.conexionBD.CERRAR();
        }
    }

    @FXML
    void buscarProductoKeyReleased(KeyEvent event) {

    }

    @FXML
    void editarProducto(ActionEvent event) throws IOException {
        if(objProducto.get()==null){
            com.inventory.appinventario.util.Metodos.rotarError(tablaProductos);
            return;
        }
        root.setEffect(new GaussianBlur(7.0));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/RegistrarProducto.fxml"));
        AnchorPane ap = loader.load();
        registrarProductoController = loader.getController();
        Scene scene = new Scene(ap);
        stageProducto = new Stage();
        stageProducto.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/productos.png")));
        stageProducto.setScene(scene);
        stageProducto.initOwner(root.getScene().getWindow());
        stageProducto.initModality(Modality.WINDOW_MODAL);
        stageProducto.initStyle(StageStyle.DECORATED);
        stageProducto.setResizable(false);
        stageProducto.setOnCloseRequest((WindowEvent e) -> {
            root.setEffect(null);
        });
        stageProducto.setOnHidden((WindowEvent e) -> {
            root.setEffect(null);
        });

        try {
            this.conexionBD.conectar();
            productoDAO = new ProductoDAO(conexionBD);
            registrarProductoController.setProducto(productoDAO.getById(objProducto.get().getIdproducto()));
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
            org.controlsfx.control.Notifications.create().title("Aviso").text("Error al intentar buscar el producto\n"+ex.getMessage()).position(Pos.CENTER).showError();
        } finally {
            this.conexionBD.CERRAR();
        }
        stageProducto.setTitle("Editar Producto");
        stageProducto.showAndWait();
        if (registrarProductoController.isSTATUS()){
            listarProductos(null);
        }



    }

    @FXML
    void listarProductos(ActionEvent event) {


        Task<List<Producto>> listTask=new Task<List<Producto>>(){
         @Override
            protected List<Producto> call() throws Exception{
             conexionBD.conectar();
             productoDAO=new ProductoDAO(conexionBD);
             return  productoDAO.getProdusctos();
         }
        };
        listTask.setOnFailed(event1->{
            conexionBD.CERRAR();
            tablaProductos.setPlaceholder(null);
        });
        listTask.setOnSucceeded(event1->{
            tablaProductos.setPlaceholder(null);
            conexionBD.CERRAR();
            listaProductos.setAll(listTask.getValue());
            tablaProductos.getColumns().forEach(column->{
                    com.inventory.appinventario.util.Metodos.changeSizeOnColumn(column, tablaProductos, -1);
            });
        });
       ProgressIndicator progressIndicator= new ProgressIndicator();
       tablaProductos.setPlaceholder(progressIndicator);

      Thread hilo=new Thread(listTask);
      hilo.start();



    }

    @FXML
    void nuevoProducto(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/RegistrarProducto.fxml"));
        AnchorPane ap = loader.load();
        registrarProductoController = loader.getController();
        Scene scene = new Scene(ap);
        stageProducto = new Stage();
        stageProducto.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/productos.png")));
        stageProducto.setScene(scene);
        stageProducto.initOwner(root.getScene().getWindow());
        stageProducto.initModality(Modality.WINDOW_MODAL);
        stageProducto.initStyle(StageStyle.DECORATED);
        stageProducto.setResizable(false);
        stageProducto.setOnCloseRequest((WindowEvent e) -> {
            root.setEffect(null);
        });
        stageProducto.setOnHidden((WindowEvent e) -> {
            root.setEffect(null);
        });
        root.setEffect(new GaussianBlur(7.0));
        stageProducto.showAndWait();
        listarProductos(null);



    }
}
