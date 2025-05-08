package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.ClienteDAO;
import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.Cliente;
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

public class ClienteController implements Initializable {

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnEditar;
    @FXML
    private Button btnExcel;

    @FXML
    private Button btnListar;

    @FXML
    private Button btnNuevo;

    @FXML
    private TextField cjBuscar;

    @FXML
    private TableColumn<Cliente, String> colApellido;

    @FXML
    private TableColumn<Cliente, String> colCorreo;

    @FXML
    private TableColumn<Cliente, Integer> colIdCliente;

    @FXML
    private TableColumn<Cliente, String> colDireccion;

    @FXML
    private TableColumn<Cliente, String> colNdocumento;

    @FXML
    private TableColumn<Cliente, String> colNombre;

    @FXML
    private TableColumn<Cliente, String> colTelefono;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Cliente> tablaClientes;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    private FilteredList<Cliente> clienteFiltrados;

    private ConexionBD conexionBD = new ConexionBD();
    private ClienteDAO clienteDAO;

    private ObjectProperty<Cliente> objCliente = new SimpleObjectProperty<>();

    private Stage stageCliente;

    private RegistrarClienteController registrarclienteController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("idcliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombrecliente"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellidocliente"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccioncliente"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonocliente"));
        colNdocumento.setCellValueFactory(new PropertyValueFactory<>("numerodocumento"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));


        clienteFiltrados = new FilteredList<>(listaClientes, p -> true);
        tablaClientes.setItems(clienteFiltrados);
        objCliente.bind(tablaClientes.getSelectionModel().selectedItemProperty());


        listarCliente(null);

    }


    @FXML
    void GenerarExcel(ActionEvent event) {
        if (listaClientes.isEmpty()) {
            org.controlsfx.control.Notifications.create()
                    .title("Aviso")
                    .text("No hay clientes para exportar.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        // Crear el libro y la hoja
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Clientes");

        // Crear encabezados
        String[] headers = {
                "Nombre", "Apellido", "N documento", "Direccion","Telefono","Correo"

        };

        // Encabezados en la primera fila
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Rellenar con datos
        int rowNum = 1;
        for (Cliente cliente : listaClientes) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(cliente.getNombrecliente());
            row.createCell(1).setCellValue(cliente.getApellidocliente());
            row.createCell(2).setCellValue(cliente.getNumerodocumento());
            row.createCell(3).setCellValue(cliente.getDireccioncliente());
            row.createCell(4).setCellValue(cliente.getTelefonocliente());
            row.createCell(5).setCellValue(cliente.getCorreo());


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
    void buscarProductoKeyReleased(KeyEvent event) {
        String texto = cjBuscar.getText().trim().toLowerCase();

        clienteFiltrados.setPredicate(cliente -> {
            if (texto.isEmpty()) {
                return true;
            }


            return cliente.getNombrecliente().toLowerCase().contains(texto)
                    || cliente.getApellidocliente().toLowerCase().contains(texto)
                    || cliente.getCorreo().toLowerCase().contains(texto)
                    || cliente.getDireccioncliente().toLowerCase().contains(texto)
                    || cliente.getNumerodocumento().toLowerCase().contains(texto);
        });
    }

    @FXML
    void editarCliente(ActionEvent event) throws IOException {

        if(objCliente.get()==null){
            com.inventory.appinventario.util.Metodos.rotarErrorCliente(tablaClientes);
            return;
        }
        root.setEffect(new GaussianBlur(7.0));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/RegistrarCliente.fxml"));
        AnchorPane ap = loader.load();
        registrarclienteController = loader.getController();
        Scene scene = new Scene(ap);
        stageCliente = new Stage();
        stageCliente.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/productos.png")));
        stageCliente.setScene(scene);
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

        try {
            this.conexionBD.conectar();
            clienteDAO = new ClienteDAO(conexionBD);
            registrarclienteController.setCliente(clienteDAO.getById(objCliente.get().getIdcliente()));
        } catch (SQLException ex) {
            Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
            org.controlsfx.control.Notifications.create().title("Aviso").text("Error al intentar buscar el CLiente\n"+ex.getMessage()).position(Pos.CENTER).showError();
        } finally {
            this.conexionBD.CERRAR();
        }
        stageCliente.setTitle("Editar Cliente");
        stageCliente.showAndWait();

        if (registrarclienteController.isSTATUS()){
            listarCliente(null);
        }



    }

    @FXML
    void listarCliente(ActionEvent event) {

        Task<List<Cliente>> listTask=new Task<List<Cliente>>(){
            @Override
            protected List<Cliente> call() throws Exception{
                conexionBD.conectar();
                clienteDAO=new ClienteDAO(conexionBD);
                return  clienteDAO.ListCliente();

            }
        };
        listTask.setOnFailed(event1->{
            conexionBD.CERRAR();
            tablaClientes.setPlaceholder(null);
        });
        listTask.setOnSucceeded(event1->{
            tablaClientes.setPlaceholder(null);
            conexionBD.CERRAR();
            listaClientes.setAll(listTask.getValue());
            tablaClientes.getColumns().forEach(column->{
                com.inventory.appinventario.util.Metodos.changeSizeOnColumn(column, tablaClientes, -1);
            });
        });
        ProgressIndicator progressIndicator= new ProgressIndicator();
        tablaClientes.setPlaceholder(progressIndicator);

        Thread hilo=new Thread(listTask);
        hilo.start();

    }

    @FXML
    void nuevoCliente(ActionEvent event) throws IOException {
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
        listarCliente(null);

    }


}