package com.inventory.appinventario.controller;

import animatefx.animation.Tada;
import com.inventory.appinventario.dao.ProductoDAO;
import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.model.Usuario;
import com.inventory.appinventario.util.ConexionBD;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrarUsuarioController implements Initializable {



    @FXML
    private Button btnGuardar;

    @FXML
    private TextField cjNombre;

    @FXML
    private PasswordField cjPassword;

    @FXML
    private TextField cjUsuario;

    @FXML
    private HBox header;

    @FXML
    private AnchorPane root;
    @FXML
    private ComboBox<String> cjRol;

    @FXML
    private Text txtUusario;

    private Usuario usuario = null;
    private ConexionBD conexionBD = new ConexionBD();

    private UsuarioDAO usuarioDAO;


    private boolean STATUS = false;
    public Usuario getPUsuario() {
        return usuario;
    }

    public boolean isSTATUS() {
        return STATUS;
    }

    public void setSTATUS(boolean STATUS) {
        this.STATUS = STATUS;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cjRol.getItems().addAll("ADMINISTRADOR", "VENDEDOR", "CAJERO");
    }


    @FXML
    void guardar(ActionEvent event) {

        if (cjUsuario.getText().isEmpty()) {
            new Tada(cjUsuario).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el  Usuario").position(Pos.CENTER).showWarning();
            return;
        }

        if (cjPassword.getText().isEmpty()) {
            new Tada(cjPassword).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el password del Usuario").position(Pos.CENTER).showWarning();
            return;
        }

        if (cjNombre.getText().isEmpty()) {
            new Tada(cjNombre).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el nombre del Usuario").position(Pos.CENTER).showWarning();
            return;
        }



        if (getPUsuario() == null) {
            this.usuario = new Usuario();
        }

        usuario.setRol(cjRol.getSelectionModel().getSelectedItem());
        usuario.setUsername(cjUsuario.getText().trim());
        usuario.setNombre(cjPassword.getText().trim());
        usuario.setPassword(cjNombre.getText().trim());


        conexionBD.conectar();
        usuarioDAO = new UsuarioDAO(conexionBD);
        try {
            int guardar = usuarioDAO.guardar(usuario);


            if ( guardar > 0 ){

                setSTATUS(true);
                com.inventory.appinventario.util.Metodos.closeEffect(root);

            }

        } catch (SQLException ex) {
            Logger.getLogger(RegistrarClienteController.class.getName()).log(Level.SEVERE, null, ex);
            org.controlsfx.control.Notifications.create().title("Aviso").text("NO SE PUDO GUARDAR EL Usuario\n" + ex.getMessage()).position(Pos.CENTER).showError();
        }finally {
            this.conexionBD.CERRAR();
        }


    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        cjUsuario.setText(usuario.getUsername().trim());
        cjPassword.setText(usuario.getPassword().trim());
        cjNombre.setText(usuario.getNombre().trim());
        cjRol.setValue(usuario.getRol());


    }


}