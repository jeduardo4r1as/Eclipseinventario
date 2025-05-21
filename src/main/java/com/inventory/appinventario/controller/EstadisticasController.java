package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.EstadisticasDAO;
import com.inventory.appinventario.util.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

public class EstadisticasController implements Initializable {

    @FXML
    private DatePicker fechaInicio;
    @FXML
    private DatePicker fechaFin;

    @FXML
    private Label lblPrendasVendidas;
    @FXML
    private Label lblIngresos;
    @FXML
    private Label lblCategoriaPopular;

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML private LineChart<String, Number> chartVentasPorDia;
    @FXML private BarChart<String, Number> chartVentasPorUsuario;
    @FXML private PieChart chartTopProductos;
    @FXML private PieChart chartFormasPago;

    @FXML private Label lblTicketPromedio;
    @FXML private Label lblFormaPagoPrincipal;

    @FXML
    private PieChart pieChart;

    private ConexionBD conexionBD = new ConexionBD();
    private EstadisticasDAO estadisticasDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            estadisticasDAO = new EstadisticasDAO(conexionBD.conectar()); // <- FUNCIONA ✅
            cargarEstadisticas(LocalDate.now().minusMonths(1), LocalDate.now());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void cargarEstadisticas(LocalDate desde, LocalDate hasta) {
        try {
            int prendas = estadisticasDAO.obtenerTotalPrendasVendidas(desde, hasta);
            double ingresos = estadisticasDAO.obtenerTotalIngresos(desde, hasta);
            Map<String, Integer> porCategoria = estadisticasDAO.obtenerVentasPorCategoria(desde, hasta);

            lblPrendasVendidas.setText(String.valueOf(prendas));
            lblIngresos.setText("$" + String.format("%,.0f", ingresos));

            String categoriaPopular = porCategoria.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Sin datos");
            lblCategoriaPopular.setText(categoriaPopular);

            // BarChart
            barChart.getData().clear();
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Ventas por categoría");
            for (Map.Entry<String, Integer> entry : porCategoria.entrySet()) {
                serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(serie);

            // PieChart
            pieChart.getData().clear();
            for (Map.Entry<String, Integer> entry : porCategoria.entrySet()) {
                pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al cargar estadísticas.").show();
        }
    }
    @FXML
    public void filtrarEstadisticas() throws SQLException {

        LocalDate desde= fechaInicio.getValue();
        LocalDate hasta= fechaFin.getValue();

// Ticket promedio
        double ticketPromedio = estadisticasDAO.obtenerTicketPromedio(desde, hasta);
        lblTicketPromedio.setText("🎫 Ticket promedio: $" + String.format("%,.0f", ticketPromedio));

// Ventas por Día
        chartVentasPorDia.getData().clear();
        XYChart.Series<String, Number> serieDia = new XYChart.Series<>();
        serieDia.setName("Ventas por día");
        for (Map.Entry<LocalDate, Double> entry : estadisticasDAO.obtenerVentasPorDia(desde, hasta).entrySet()) {
            serieDia.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        chartVentasPorDia.getData().add(serieDia);

// Ventas por Usuario
        chartVentasPorUsuario.getData().clear();
        XYChart.Series<String, Number> serieUsuario = new XYChart.Series<>();
        serieUsuario.setName("Ventas por vendedor");
        for (Map.Entry<String, Double> entry : estadisticasDAO.obtenerVentasPorUsuario(desde, hasta).entrySet()) {
            serieUsuario.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        chartVentasPorUsuario.getData().add(serieUsuario);

// Top productos
        chartTopProductos.getData().clear();
        for (Map.Entry<String, Integer> entry : estadisticasDAO.obtenerTopProductos(desde, hasta).entrySet()) {
            chartTopProductos.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

// Formas de pago
        chartFormasPago.getData().clear();
        Map<String, Integer> formasPago = estadisticasDAO.obtenerFormasDePago(desde, hasta);
        for (Map.Entry<String, Integer> entry : formasPago.entrySet()) {
            chartFormasPago.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

// Mostrar forma de pago más usada
        formasPago.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> lblFormaPagoPrincipal.setText("💳 Forma de pago más usada: " + e.getKey()));

    }
}
