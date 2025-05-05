package com.inventory.appinventario.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {

    private final IntegerProperty idusuario = new SimpleIntegerProperty(0);
    private final StringProperty username= new SimpleStringProperty();
    private final StringProperty password= new SimpleStringProperty();
    private final StringProperty nombre= new SimpleStringProperty();

    private final StringProperty rol= new SimpleStringProperty();

    private final StringProperty estado= new SimpleStringProperty();

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public String getRol() {
        return rol.get();
    }

    public StringProperty rolProperty() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol.set(rol);
    }

    public int getIdusuario() {
        return idusuario.get();
    }

    public IntegerProperty idusuarioProperty() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario.set(idusuario);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
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
}
