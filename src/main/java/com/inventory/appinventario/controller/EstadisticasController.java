package com.inventory.appinventario.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EstadisticasController implements Initializable {

    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;

    @FXML private Label lblPrendasVendidas;
    @FXML private Label lblIngresos;
    @FXML private Label lblCategoriaPopular;

    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private PieChart pieChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarEstadisticas(LocalDate.now().minusMonths(1), LocalDate.now());
    }

    @FXML
    public void filtrarEstadisticas() {
        LocalDate desde = fechaInicio.getValue();
        LocalDate hasta = fechaFin.getValue();

        if (desde != null && hasta != null && !desde.isAfter(hasta)) {
            cargarEstadisticas(desde, hasta);
        } else {
            new Alert(Alert.AlertType.WARNING, "Rango de fechas no válido.").show();
        }
    }

    private void cargarEstadisticas(LocalDate desde, LocalDate hasta) {
        // Aquí iría la lógica de consulta a tu base de datos

        // 🔢 Valores simulados para ejemplo
        lblPrendasVendidas.setText("784");
        lblIngresos.setText("$16.580.000");
        lblCategoriaPopular.setText("Camisas");

        // 📊 BarChart: Cantidad por categoría
        barChart.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ventas por categoría");

        serie.getData().add(new XYChart.Data<>("Camisas", 310));
        serie.getData().add(new XYChart.Data<>("Pantalones", 220));
        serie.getData().add(new XYChart.Data<>("Chaquetas", 120));
        serie.getData().add(new XYChart.Data<>("Zapatos", 134));

        barChart.getData().add(serie);

        // 🥧 PieChart: Proporciones
        pieChart.getData().clear();
        pieChart.getData().add(new PieChart.Data("Camisas", 40));
        pieChart.getData().add(new PieChart.Data("Pantalones", 28));
        pieChart.getData().add(new PieChart.Data("Chaquetas", 15));
        pieChart.getData().add(new PieChart.Data("Zapatos", 17));
    }


}