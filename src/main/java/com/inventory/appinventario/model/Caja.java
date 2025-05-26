package com.inventory.appinventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Caja {


    private final SimpleStringProperty nombreCliente= new SimpleStringProperty();
    private final SimpleStringProperty correo = new SimpleStringProperty();
    private final IntegerProperty numeroFactura= new SimpleIntegerProperty(0);;
    private final SimpleStringProperty fechaDeVenta= new SimpleStringProperty();
    private final SimpleDoubleProperty total= new SimpleDoubleProperty();
    private final SimpleStringProperty vendedor = new SimpleStringProperty();
    private final SimpleDoubleProperty subTotal= new SimpleDoubleProperty();

    public Caja(String nombrecliente, String correo, String numerofactura, String fechadeventa, double total, String nombre, double subtotal) {
        this.nombreCliente.set(nombrecliente);
        this.correo.set(correo);
        this.numeroFactura.set(Integer.parseInt(numerofactura));
        this.fechaDeVenta.set(fechadeventa);
        this.total.set(total);
        this.vendedor.set(nombre);
        this.subTotal.set(subtotal);
    }

    public String getNombreCliente() {
        return nombreCliente.get();
    }

    public SimpleStringProperty nombreClienteProperty() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente.set(nombreCliente);
    }

    public String getCorreo() {
        return correo.get();
    }

    public SimpleStringProperty correoProperty() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public int getNumeroFactura() {
        return numeroFactura.get();
    }

    public IntegerProperty numeroFacturaProperty() {
        return numeroFactura;
    }

    public void setNumeroFactura(int numeroFactura) {
        this.numeroFactura.set(numeroFactura);
    }

    public String getFechaDeVenta() {
        return fechaDeVenta.get();
    }

    public SimpleStringProperty fechaDeVentaProperty() {
        return fechaDeVenta;
    }

    public void setFechaDeVenta(String fechaDeVenta) {
        this.fechaDeVenta.set(fechaDeVenta);
    }

    public double getTotal() {
        return total.get();
    }

    public SimpleDoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public String getVendedor() {
        return vendedor.get();
    }

    public SimpleStringProperty vendedorProperty() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor.set(vendedor);
    }

    public double getSubTotal() {
        return subTotal.get();
    }

    public SimpleDoubleProperty subTotalProperty() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal.set(subTotal);
    }
}
