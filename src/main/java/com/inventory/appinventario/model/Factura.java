package com.inventory.appinventario.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Factura {

    private final SimpleStringProperty nombreCliente;
    private final SimpleStringProperty correo;
    private final SimpleStringProperty numeroFactura;
    private final SimpleStringProperty fechaDeVenta;
    private final SimpleDoubleProperty total;
    private final SimpleStringProperty vendedor;
   private final SimpleDoubleProperty subTotal;

    public Factura(String nombreCliente, String correo, String numeroFactura, String fechaDeVenta, double total, String nombre, double subTotal) {
        this.nombreCliente = new SimpleStringProperty(nombreCliente);
        this.correo = new SimpleStringProperty(correo);
        this.numeroFactura = new SimpleStringProperty(numeroFactura);
        this.fechaDeVenta = new SimpleStringProperty(fechaDeVenta);
        this.total = new SimpleDoubleProperty(total);
        this.vendedor = new SimpleStringProperty(nombre);
        this.subTotal = new SimpleDoubleProperty(subTotal);
    }

    public Factura(SimpleStringProperty nombreCliente, SimpleStringProperty correo, SimpleStringProperty numeroFactura, SimpleStringProperty fechaDeVenta, SimpleDoubleProperty total, SimpleStringProperty nombre, SimpleDoubleProperty subTotal) {
        this.nombreCliente = nombreCliente;
        this.correo = correo;
        this.numeroFactura = numeroFactura;
        this.fechaDeVenta = fechaDeVenta;
        this.total = total;
        this.vendedor = nombre;
        this.subTotal = subTotal;
    }


    public String getNombreCliente() { return nombreCliente.get(); }
    public String getCorreo() { return correo.get(); }
    public String getNumeroFactura() { return numeroFactura.get(); }
    public String getFechaDeVenta() { return fechaDeVenta.get(); }
    public double getTotal() { return total.get(); }
    public String getVendedor() { return vendedor.get(); }
    public double getSubTotal() { return subTotal.get(); }
}
