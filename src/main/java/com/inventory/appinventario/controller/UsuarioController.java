package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.ProductoDAO;
import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.model.Usuario;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioController implements Initializable {

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnListar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnActivar;

    @FXML
    private TextField cjBuscar;

    @FXML
    private TableColumn<Usuario, Integer> colId;

    @FXML
    private TableColumn<Usuario, String> colNombre;

    @FXML
    private TableColumn<Usuario, String> colNombreU;

    @FXML
    private TableColumn<Usuario, String> colPassWord;

    @FXML
    private TableColumn<Usuario, String> colRol;

    @FXML
    private TableColumn<Usuario, String> colEstado;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();


    private ConexionBD conexionBD = new ConexionBD();
    private UsuarioDAO usuarioDAO;

    private ObjectProperty<Usuario> objUsuario = new SimpleObjectProperty<>();

    private Stage stageUsuario;

    private RegistrarUsuarioController registrarusuarioController;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        colId.setCellValueFactory(new PropertyValueFactory<>("idusuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombreU.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassWord.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaUsuarios.setItems(listaUsuarios);
        objUsuario.bind(tablaUsuarios.getSelectionModel().selectedItemProperty());
    }

    @FXML
    void borrarUsuario(ActionEvent event) throws SQLException {

        if(objUsuario.get()==null){
            com.inventory.appinventario.util.Metodos.rotarErrorUsuario(tablaUsuarios);
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿DESEA DESACTIVAR EL USUARIO?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(this.objUsuario.get().getNombre());
        if (a.showAndWait().get() == ButtonType.YES) {
            conexionBD.conectar();
            usuarioDAO = new UsuarioDAO(conexionBD);
            boolean delete = usuarioDAO.delete(objUsuario.get().getIdusuario());
            listarUsuarios(null);
            this.conexionBD.CERRAR();
        }

    }

    @FXML
    void buscarProductoKeyReleased(KeyEvent event) {

    }

    @FXML
    void editarUsuario(ActionEvent event) throws IOException {

        if(objUsuario.get()==null){
            com.inventory.appinventario.util.Metodos.rotarErrorUsuario(tablaUsuarios);
            return;
        }
        root.setEffect(new GaussianBlur(7.0));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/RegistrarUsuario.fxml"));
        AnchorPane ap = loader.load();
        registrarusuarioController = loader.getController();
        Scene scene = new Scene(ap);
        stageUsuario = new Stage();
        stageUsuario.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/productos.png")));
        stageUsuario.setScene(scene);
        stageUsuario.initOwner(root.getScene().getWindow());
        stageUsuario.initModality(Modality.WINDOW_MODAL);
        stageUsuario.initStyle(StageStyle.DECORATED);
        stageUsuario.setResizable(false);
        stageUsuario.setOnCloseRequest((WindowEvent e) -> {
            root.setEffect(null);
        });
        stageUsuario.setOnHidden((WindowEvent e) -> {
            root.setEffect(null);
        });

        try {
            this.conexionBD.conectar();
            usuarioDAO = new UsuarioDAO(conexionBD);
            registrarusuarioController.setUsuario(usuarioDAO.getById(objUsuario.get().getIdusuario()));
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
            org.controlsfx.control.Notifications.create().title("Aviso").text("Error al intentar buscar el usuario\n"+ex.getMessage()).position(Pos.CENTER).showError();
        } finally {
            this.conexionBD.CERRAR();
        }
        stageUsuario.setTitle("Editar Usuario");
        stageUsuario.showAndWait();
        if (registrarusuarioController.isSTATUS()){
            listarUsuarios(null);
        }



    }

    @FXML
    void listarUsuarios(ActionEvent event) {

        Task<List<Usuario>> listTask=new Task<List<Usuario>>(){
            @Override
            protected List<Usuario> call() throws Exception{
                conexionBD.conectar();
                usuarioDAO=new UsuarioDAO(conexionBD);
                return  usuarioDAO.ListUsuarios();
            }
        };
        listTask.setOnFailed(event1->{
            conexionBD.CERRAR();
            tablaUsuarios.setPlaceholder(null);
        });
        listTask.setOnSucceeded(event1->{
            tablaUsuarios.setPlaceholder(null);
            conexionBD.CERRAR();
            listaUsuarios.setAll(listTask.getValue());
            tablaUsuarios.getColumns().forEach(column->{
                com.inventory.appinventario.util.Metodos.changeSizeOnColumn(column, tablaUsuarios, -1);
            });
        });
        ProgressIndicator progressIndicator= new ProgressIndicator();
        tablaUsuarios.setPlaceholder(progressIndicator);

        Thread hilo=new Thread(listTask);
        hilo.start();


    }

    @FXML
    void nuevoUsuario(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/RegistrarUsuario.fxml"));
        AnchorPane ap = loader.load();
        registrarusuarioController = loader.getController();
        Scene scene = new Scene(ap);
        stageUsuario = new Stage();
        stageUsuario.setScene(scene);
        stageUsuario.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/add_user.png")));
        stageUsuario.initOwner(root.getScene().getWindow());
        stageUsuario.initModality(Modality.WINDOW_MODAL);
        stageUsuario.initStyle(StageStyle.DECORATED);
        stageUsuario.setResizable(false);
        stageUsuario.setOnCloseRequest((WindowEvent e) -> {
            root.setEffect(null);
        });
        stageUsuario.setOnHidden((WindowEvent e) -> {
            root.setEffect(null);
        });
        root.setEffect(new GaussianBlur(7.0));
        stageUsuario.showAndWait();
        listarUsuarios(null);
    }

    @FXML
    void ActivarUsuario(ActionEvent event) throws SQLException {

        if(objUsuario.get()==null){
            com.inventory.appinventario.util.Metodos.rotarErrorUsuario(tablaUsuarios);
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿DESEA ACTIVAR EL USUARIO?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(this.objUsuario.get().getNombre());
        if (a.showAndWait().get() == ButtonType.YES) {
            conexionBD.conectar();
            usuarioDAO = new UsuarioDAO(conexionBD);
            boolean delete = usuarioDAO.activar(objUsuario.get().getIdusuario());
            listarUsuarios(null);
            this.conexionBD.CERRAR();
        }

    }


}