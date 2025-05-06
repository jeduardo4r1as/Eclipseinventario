package com.inventory.appinventario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfiguracionController implements Initializable {
    @FXML
    private CheckBox chkNotificaciones;

    @FXML
    private ComboBox<?> comboFormatoFecha;

    @FXML
    private ComboBox<?> comboIdioma;

    @FXML
    private ComboBox<?> comboTema;

    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPuerto;

    @FXML
    private TextField txtRutaArchivos;

    @FXML
    private TextField txtServidor;

    @FXML
    private TextField txtTimeout;

    @FXML
    void cancelarCambios(ActionEvent event) {

    }

    @FXML
    void guardarCambios(ActionEvent event) {

    }

    @FXML
    void restaurarValores(ActionEvent event) {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


}
