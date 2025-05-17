package com.inventory.appinventario.model;

import javafx.beans.property.*;

public class DetalleFactura {
    private final StringProperty producto;
    private final DoubleProperty cantidad;
    private final DoubleProperty preciodeventa;
    private final DoubleProperty subtotal;
    private final StringProperty tipoprecio;
    private final StringProperty descripcion;
    private final StringProperty talla;

    public DetalleFactura(String producto, double cantidad, double preciodeventa, double subtotal,
                          String tipoprecio, String descripcion, String talla) {
        this.producto = new SimpleStringProperty(producto);
        this.cantidad = new SimpleDoubleProperty(cantidad);
        this.preciodeventa = new SimpleDoubleProperty(preciodeventa);
        this.subtotal = new SimpleDoubleProperty(subtotal);
        this.tipoprecio = new SimpleStringProperty(tipoprecio);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.talla = new SimpleStringProperty(talla);
    }

    public String getProducto() { return producto.get(); }
    public double getCantidad() { return cantidad.get(); }
    public double getPreciodeventa() { return preciodeventa.get(); }
    public double getSubtotal() { return subtotal.get(); }
    public String getTipoprecio() { return tipoprecio.get(); }
    public String getDescripcion() { return descripcion.get(); }
    public String getTalla() { return talla.get(); }
}





