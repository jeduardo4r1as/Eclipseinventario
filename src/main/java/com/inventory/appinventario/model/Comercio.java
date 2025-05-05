package com.inventory.appinventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Comercio {

    private final IntegerProperty idcomercio = new SimpleIntegerProperty(0);
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty nit = new SimpleStringProperty();
    private final StringProperty direccion = new SimpleStringProperty();
    private final StringProperty telefono = new SimpleStringProperty();
    private final IntegerProperty iva = new SimpleIntegerProperty(0);

    private static Comercio comercio;

    public static Comercio getInstance(Comercio c){
        if(comercio == null){
            comercio = c;
        }
        return comercio;
    }
    public int getIdcomercio() {
        return idcomercio.get();
    }

    public IntegerProperty idcomercioProperty() {
        return idcomercio;
    }

    public void setIdcomercio(int idcomercio) {
        this.idcomercio.set(idcomercio);
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getNit() {
        return nit.get();
    }

    public StringProperty nitProperty() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit.set(nit);
    }

    public String getDireccion() {
        return direccion.get();
    }

    public StringProperty direccionProperty() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion.set(direccion);
    }

    public String getTelefono() {
        return telefono.get();
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono.set(telefono);
    }

    public int getIva() {
        return iva.get();
    }

    public IntegerProperty ivaProperty() {
        return iva;
    }

    public void setIva(int iva) {
        this.iva.set(iva);
    }
}
