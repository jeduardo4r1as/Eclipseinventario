package com.inventory.appinventario.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Factura {

    private final SimpleStringProperty nombreCliente;
    private final SimpleStringProperty numeroFactura;
    private final SimpleStringProperty fechaDeVenta;
    private final SimpleDoubleProperty total;
    private final SimpleStringProperty vendedor;
    private final SimpleDoubleProperty iva;
    private final SimpleDoubleProperty subTotal;

    public Factura(String nombreCliente, String numeroFactura, String fechaDeVenta, double total, String nombre, double iva, double subTotal) {
        this.nombreCliente = new SimpleStringProperty(nombreCliente);
        this.numeroFactura = new SimpleStringProperty(numeroFactura);
        this.fechaDeVenta = new SimpleStringProperty(fechaDeVenta);
        this.total = new SimpleDoubleProperty(total);
        this.vendedor = new SimpleStringProperty(nombre);
        this.iva = new SimpleDoubleProperty(iva);
        this.subTotal = new SimpleDoubleProperty(subTotal);
    }

    public Factura(SimpleStringProperty nombreCliente, SimpleStringProperty numeroFactura, SimpleStringProperty fechaDeVenta, SimpleDoubleProperty total, SimpleStringProperty nombre, SimpleDoubleProperty iva, SimpleDoubleProperty subTotal) {
        this.nombreCliente = nombreCliente;
        this.numeroFactura = numeroFactura;
        this.fechaDeVenta = fechaDeVenta;
        this.total = total;
        this.vendedor = nombre;
        this.iva = iva;
        this.subTotal = subTotal;
    }


    public String getNombreCliente() { return nombreCliente.get(); }
    public String getNumeroFactura() { return numeroFactura.get(); }
    public String getFechaDeVenta() { return fechaDeVenta.get(); }
    public double getTotal() { return total.get(); }
    public String getVendedor() { return vendedor.get(); }
    public double getIva() { return iva.get(); }
    public double getSubTotal() { return subTotal.get(); }
}
