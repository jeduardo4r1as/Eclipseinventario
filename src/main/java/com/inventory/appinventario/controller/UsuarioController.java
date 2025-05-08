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
import javafx.collections.transformation.FilteredList;
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
import javafx.stage.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
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
    private Button btnExcel;

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


    private FilteredList<Usuario> usuariosFiltrados;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        colId.setCellValueFactory(new PropertyValueFactory<>("idusuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombreU.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassWord.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));


        usuariosFiltrados = new FilteredList<>(listaUsuarios, p -> true);
        tablaUsuarios.setItems(usuariosFiltrados);
        objUsuario.bind(tablaUsuarios.getSelectionModel().selectedItemProperty());

        listarUsuarios(null);
    }

    @FXML
    void GenerarExcel(ActionEvent event) {
        if (listaUsuarios.isEmpty()) {
            org.controlsfx.control.Notifications.create()
                    .title("Aviso")
                    .text("No hay usuarios para exportar.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        // Crear el libro y la hoja
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Usuarios");

        // Crear encabezados
        String[] headers = {
                "Nombre", "Nombre de usuario", "Rol", "Estado"

        };

        // Encabezados en la primera fila
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Rellenar con datos
        int rowNum = 1;
        for (Usuario usuario : listaUsuarios) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(usuario.getNombre());
            row.createCell(1).setCellValue(usuario.getUsername());
            row.createCell(2).setCellValue(usuario.getRol());
            row.createCell(3).setCellValue(usuario.getEstado());


        }

        // Autosize columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar el archivo
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(root.getScene().getWindow());

            if (file != null) {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();

                org.controlsfx.control.Notifications.create()
                        .title("Éxito")
                        .text("Archivo Excel generado correctamente.")
                        .position(Pos.CENTER)
                        .showInformation();
            }

        } catch (IOException e) {
            org.controlsfx.control.Notifications.create()
                    .title("Error")
                    .text("Error al generar el archivo Excel: " + e.getMessage())
                    .position(Pos.CENTER)
                    .showError();
            e.printStackTrace();
        }
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

        String texto = cjBuscar.getText().trim().toLowerCase();

        usuariosFiltrados.setPredicate(usuario -> {
            if (texto.isEmpty()) {
                return true;
            }


            return usuario.getNombre().toLowerCase().contains(texto)
                    || usuario.getUsername().toLowerCase().contains(texto)
                    || usuario.getEstado().toLowerCase().contains(texto)
                    || usuario.getRol().toLowerCase().contains(texto);
        });

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