package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.CajaDAO;
import com.inventory.appinventario.dao.ClienteDAO;
import com.inventory.appinventario.model.Caja;
import com.inventory.appinventario.util.ConexionBD;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CajaController implements Initializable {
    @FXML
    private Button btnExcel;

    @FXML
    private TableColumn<Caja, Integer> colCodigo;

    @FXML
    private TableColumn<Caja, String> colCorreo;

    @FXML
    private TableColumn<Caja, String> colFechaVenta;

    @FXML
    private TableColumn<Caja, String> colNombre;

    @FXML
    private TableColumn<Caja, Double> colSubTotal;

    @FXML
    private TableColumn<Caja, Double> colTotal;

    @FXML
    private TableColumn<Caja, String> colVendedor;

    @FXML
    private DatePicker fechaInicio;

    @FXML
    private TableView<Caja> tablaFacturas;

    @FXML
    private Text txtNumeroFacturas;

    @FXML
    private Text txtSubtotal;

    @FXML
    private Text txtTotal;

    @FXML
    private AnchorPane root;
    private ConexionBD conexionBD = new ConexionBD();
    private CajaDAO cajaDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaDeVenta"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colVendedor.setCellValueFactory(new PropertyValueFactory<>("vendedor"));
        colSubTotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));

    }

    @FXML
    void GenerarExcel(ActionEvent event) throws IOException {
        List<Caja> ventas = tablaFacturas.getItems();

        if (ventas == null || ventas.isEmpty()) {
            org.controlsfx.control.Notifications.create()
                    .title("Exportación")
                    .text("No hay datos para exportar.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ventas del Día");

            // Encabezados
            String[] headers = {"N° Factura", "Cliente", "Correo", "Fecha Venta", "Total", "Vendedor", "Subtotal"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Datos
            for (int i = 0; i < ventas.size(); i++) {
                Caja caja = ventas.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(caja.getNumeroFactura());
                row.createCell(1).setCellValue(caja.getNombreCliente());
                row.createCell(2).setCellValue(caja.getCorreo());
                row.createCell(3).setCellValue(caja.getFechaDeVenta());
                row.createCell(4).setCellValue(caja.getTotal());
                row.createCell(5).setCellValue(caja.getVendedor());
                row.createCell(6).setCellValue(caja.getSubTotal());
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(btnExcel.getScene().getWindow());

            if (file != null) {
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                    org.controlsfx.control.Notifications.create()
                            .title("Éxito")
                            .text("Archivo Excel generado correctamente.")
                            .position(Pos.CENTER)
                            .showInformation();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            org.controlsfx.control.Notifications.create()
                    .title("Error")
                    .text("No se pudo exportar a Excel.\n" + e.getMessage())
                    .position(Pos.CENTER)
                    .showError();
        }
    }



    @FXML
    void filtrarEstadisticas(ActionEvent event) {
        LocalDate fechaSeleccionada = fechaInicio.getValue(); // Asegúrate de tener un DatePicker con fx:id="datePickerFecha"

        this.conexionBD.conectar();
        cajaDAO = new CajaDAO(conexionBD);

        if (fechaSeleccionada == null) {
            org.controlsfx.control.Notifications.create()
                    .title("Filtro")
                    .text("Por favor, selecciona una fecha.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        try {
            List<Caja> listaFiltrada = cajaDAO.listarFacturas(fechaSeleccionada); // ← tu método corregido con parámetro
            tablaFacturas.getItems().setAll(listaFiltrada); // ← tu TableView, cambia el fx:id si es diferente

            int cantidadFacturas = listaFiltrada.size();
            double total = listaFiltrada.stream().mapToDouble(Caja::getTotal).sum();
            double subtotal = listaFiltrada.stream().mapToDouble(Caja::getSubTotal).sum();

            txtTotal.setText(String.format("$ %, .2f", total));
            txtSubtotal.setText(String.format("$ %, .2f", subtotal));
            txtNumeroFacturas.setText(String.valueOf(cantidadFacturas));

            if (listaFiltrada.isEmpty()) {
                org.controlsfx.control.Notifications.create()
                        .title("Resultado")
                        .text("No se encontraron facturas para la fecha seleccionada.")
                        .position(Pos.CENTER)
                        .showInformation();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            org.controlsfx.control.Notifications.create()
                    .title("Error")
                    .text("No se pudo filtrar la información.\n" + e.getMessage())
                    .position(Pos.CENTER)
                    .showError();
        }

    }

}
