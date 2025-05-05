package com.inventory.appinventario.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Cliente {
    private final IntegerProperty idcliente = new SimpleIntegerProperty(0);
    private final StringProperty nombrecliente = new SimpleStringProperty();
    private final StringProperty numerodocumento = new SimpleStringProperty();
    private final StringProperty apellidocliente = new SimpleStringProperty();
    private final StringProperty direccioncliente = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> fechaderegistro = new SimpleObjectProperty<>();
    private final StringProperty telefonocliente = new SimpleStringProperty();

    private final StringProperty correo = new SimpleStringProperty();

    public String getCorreo() {
        return correo.get();
    }

    public StringProperty correoProperty() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public int getIdcliente() {
        return idcliente.get();
    }

    public IntegerProperty idclienteProperty() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente.set(idcliente);
    }

    public String getNombrecliente() {
        return nombrecliente.get();
    }

    public StringProperty nombreclienteProperty() {
        return nombrecliente;
    }

    public void setNombrecliente(String nombrecliente) {
        this.nombrecliente.set(nombrecliente);
    }

    public String getNumerodocumento() {
        return numerodocumento.get();
    }

    public StringProperty numerodocumentoProperty() {
        return numerodocumento;
    }

    public void setNumerodocumento(String numerodocumento) {
        this.numerodocumento.set(numerodocumento);
    }

    public String getApellidocliente() {
        return apellidocliente.get();
    }

    public StringProperty apellidoclienteProperty() {
        return apellidocliente;
    }

    public void setApellidocliente(String apellidocliente) {
        this.apellidocliente.set(apellidocliente);
    }

    public String getDireccioncliente() {
        return direccioncliente.get();
    }

    public StringProperty direccionclienteProperty() {
        return direccioncliente;
    }

    public void setDireccioncliente(String direccioncliente) {
        this.direccioncliente.set(direccioncliente);
    }

    public LocalDateTime getFechaderegistro() {
        return fechaderegistro.get();
    }

    public ObjectProperty<LocalDateTime> fechaderegistroProperty() {
        return fechaderegistro;
    }

    public void setFechaderegistro(LocalDateTime fechaderegistro) {
        this.fechaderegistro.set(fechaderegistro);
    }

    public String getTelefonocliente() {
        return telefonocliente.get();
    }

    public StringProperty telefonoclienteProperty() {
        return telefonocliente;
    }

    public void setTelefonocliente(String telefonocliente) {
        this.telefonocliente.set(telefonocliente);
    }


    @Override
    public String toString() {
        return getNombrecliente() + " " + getApellidocliente();
    }
}
