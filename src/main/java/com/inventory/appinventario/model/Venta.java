package com.inventory.appinventario.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Venta {

    private final IntegerProperty idventa = new SimpleIntegerProperty();
    private Cliente cliente;
    private Usuario usuario;
    private final ObjectProperty<LocalDateTime> fecha = new SimpleObjectProperty<>();
    private final IntegerProperty numerofactura = new SimpleIntegerProperty();
    private final StringProperty formadepago = new SimpleStringProperty();
    private final DoubleProperty subtotal = new SimpleDoubleProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final DoubleProperty iva = new SimpleDoubleProperty();


    public double getSubtotal() {
        return subtotal.get();
    }

    public DoubleProperty subtotalProperty() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal.set(subtotal);
    }

    public double getTotal() {
        return total.get();
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public double getIva() {
        return iva.get();
    }

    public DoubleProperty ivaProperty() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva.set(iva);
    }

    public int getIdventa() {
        return idventa.get();
    }

    public IntegerProperty idventaProperty() {
        return idventa;
    }

    public void setIdventa(int idventa) {
        this.idventa.set(idventa);
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    private List<DetalleVenta> detalleventa = new ArrayList<>();

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFecha() {
        return fecha.get();
    }

    public ObjectProperty<LocalDateTime> fechaProperty() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha.set(fecha);
    }

    public int getNumerofactura() {
        return numerofactura.get();
    }

    public IntegerProperty numerofacturaProperty() {
        return numerofactura;
    }

    public void setNumerofactura(int numerofactura) {
        this.numerofactura.set(numerofactura);
    }

    public String getFormadepago() {
        return formadepago.get();
    }

    public StringProperty formadepagoProperty() {
        return formadepago;
    }

    public void setFormadepago(String formadepago) {
        this.formadepago.set(formadepago);
    }

    public List<DetalleVenta> getDetalleventa() {
        return detalleventa;
    }

    public void setDetalleventa(List<DetalleVenta> detalleventa) {
        this.detalleventa = detalleventa;
    }
}
