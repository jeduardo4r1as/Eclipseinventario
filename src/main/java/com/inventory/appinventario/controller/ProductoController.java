package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.ProductoDAO;
import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.util.ConexionBD;
import com.inventory.appinventario.util.Sesion;
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

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



// Java I/O (para guardar archivo)
import java.io.IOException;

public class ProductoController implements Initializable {



    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnExcel;

    @FXML
    private Button btnPdf;

    @FXML
    private Button btnListar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnActivar;

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

    private FilteredList<Producto> productosFiltrados;

    private ConexionBD conexionBD = new ConexionBD();
    private ProductoDAO productoDAO;

    private Stage stageProducto;
    private RegistrarProductoController registrarProductoController;

    private ObjectProperty<Producto> objProducto = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String rol = Sesion.getRol();

        if ("VENDEDOR".equalsIgnoreCase(rol)) {
            // opciones que se le ocultan al vendedor
            btnBorrar.setVisible(false);
            btnEditar.setVisible(false);
            btnNuevo.setVisible(false);
            btnActivar.setVisible(false);



        }

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

        productosFiltrados = new FilteredList<>(listaProductos, p -> true);
        tablaProductos.setItems(productosFiltrados);
        objProducto.bind(tablaProductos.getSelectionModel().selectedItemProperty());

        listarProductos(null);

        }


    @FXML
    void GenerarExcel(ActionEvent event) {
        if (listaProductos.isEmpty()) {
            org.controlsfx.control.Notifications.create()
                    .title("Aviso")
                    .text("No hay productos para exportar.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        // Crear el libro y la hoja
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Productos");

        // Crear encabezados
        String[] headers = {
                "Nombre", "Código", "Referencia", "Descripción",
                "Talla", "Género", "Precio Unitario", "Precio Mayorista",
                "Precio Distribuidor", "Stock", "Stock Mínimo", "Estado", "Fecha Registro"
        };

        // Encabezados en la primera fila
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Rellenar con datos
        int rowNum = 1;
        for (Producto producto : listaProductos) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(producto.getNombreproducto());
            row.createCell(1).setCellValue(producto.getCodigodebarras());
            row.createCell(2).setCellValue(producto.getReferencia());
            row.createCell(3).setCellValue(producto.getDescripcion());
            row.createCell(4).setCellValue(producto.getTalla());
            row.createCell(5).setCellValue(producto.getGenero());
            row.createCell(6).setCellValue(producto.getPreciounitario());
            row.createCell(7).setCellValue(producto.getPreciomayorista());
            row.createCell(8).setCellValue(producto.getPreciodistribuidor());
            row.createCell(9).setCellValue(producto.getStock());
            row.createCell(10).setCellValue(producto.getStockminimo());
            row.createCell(11).setCellValue(producto.getEstado());
            row.createCell(12).setCellValue(
                    producto.getFechaderegistro() != null ? producto.getFechaderegistro().toString() : ""
            );
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
    void GenerarPdf(ActionEvent event) {
        if (objProducto.get() == null) {
            com.inventory.appinventario.util.Metodos.rotarError(tablaProductos);
            org.controlsfx.control.Notifications.create()
                    .title("Aviso")
                    .text("Debes seleccionar un producto para generar el PDF")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        try {

            InputStream reportStream = getClass().getResourceAsStream("/reports/productoindivi.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parametros = new HashMap<>();
            Producto producto = objProducto.get();

            parametros.put("nombreproducto", producto.getNombreproducto());
            parametros.put("codigodebarras", producto.getCodigodebarras());
            parametros.put("descripcion", producto.getDescripcion());
            parametros.put("talla", producto.getTalla());
            parametros.put("genero", producto.getGenero());
            parametros.put("preciounitario", producto.getPreciounitario());
            parametros.put("preciomayorista", producto.getPreciomayorista());
            parametros.put("preciodistribuidor", producto.getPreciodistribuidor());
            parametros.put("stock", producto.getStock());

            if (producto.getImagen() != null) {
                InputStream fotoStream = new ByteArrayInputStream(producto.getImagen());
                parametros.put("imagenProducto", fotoStream);
            } else {

                InputStream fotoStream = getClass().getResourceAsStream("/img/productos.png");
                parametros.put("imagenProducto", fotoStream);
            }


            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());


            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar reporte PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
            fileChooser.setInitialFileName("Producto_" + producto.getIdproducto() + ".pdf");

            File archivoDestino = fileChooser.showSaveDialog(root.getScene().getWindow());

            if (archivoDestino != null) {
                JasperExportManager.exportReportToPdfFile(jasperPrint, archivoDestino.getAbsolutePath());


                org.controlsfx.control.Notifications.create()
                        .title("Exportación exitosa")
                        .text("El PDF fue guardado en:\n" + archivoDestino.getAbsolutePath())
                        .position(Pos.CENTER)
                        .showInformation();
            } else {

                org.controlsfx.control.Notifications.create()
                        .title("Guardado cancelado")
                        .text("No se ha guardado ningún archivo.")
                        .position(Pos.CENTER)
                        .showWarning();
            }

        } catch (Exception e) {
            e.printStackTrace();
            org.controlsfx.control.Notifications.create()
                    .title("Error")
                    .text("Ocurrió un error al generar el PDF:\n" + e.getMessage())
                    .position(Pos.CENTER)
                    .showError();
        }
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
        String texto = cjBuscar.getText().trim().toLowerCase();

        productosFiltrados.setPredicate(producto -> {
            if (texto.isEmpty()) {
                return true;
            }


            return producto.getNombreproducto().toLowerCase().contains(texto)
                    || producto.getCodigodebarras().toLowerCase().contains(texto)
                    || producto.getReferencia().toLowerCase().contains(texto)
                    || producto.getDescripcion().toLowerCase().contains(texto);
        });
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
