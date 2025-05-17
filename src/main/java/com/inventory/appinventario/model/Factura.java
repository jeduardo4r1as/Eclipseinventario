package com.inventory.appinventario.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Factura {

    private final SimpleStringProperty nombreCliente;
    private final SimpleStringProperty numeroFactura;
    private final SimpleStringProperty fechaDeVenta;
    private final SimpleDoubleProperty total;

    public Factura(String nombreCliente, String numeroFactura, String fechaDeVenta, double total) {
        this.nombreCliente = new SimpleStringProperty(nombreCliente);
        this.numeroFactura = new SimpleStringProperty(numeroFactura);
        this.fechaDeVenta = new SimpleStringProperty(fechaDeVenta);
        this.total = new SimpleDoubleProperty(total);
    }

    public Factura(SimpleStringProperty nombreCliente, SimpleStringProperty numeroFactura, SimpleStringProperty fechaDeVenta, SimpleDoubleProperty total) {
        this.nombreCliente = nombreCliente;
        this.numeroFactura = numeroFactura;
        this.fechaDeVenta = fechaDeVenta;
        this.total = total;
    }

    public String getNombreCliente() { return nombreCliente.get(); }
    public String getNumeroFactura() { return numeroFactura.get(); }
    public String getFechaDeVenta() { return fechaDeVenta.get(); }
    public double getTotal() { return total.get(); }
}
