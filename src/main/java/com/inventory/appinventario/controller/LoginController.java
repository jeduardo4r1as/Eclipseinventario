package com.inventory.appinventario.controller;

import com.inventory.appinventario.dao.UsuarioDAO;
import com.inventory.appinventario.model.Usuario;
import com.inventory.appinventario.util.ConexionBD;
import com.inventory.appinventario.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button btnIniciar;

    @FXML
    private PasswordField cjPassword;

    @FXML
    private TextField cjUsername;

    @FXML
    private AnchorPane root;

    private ConexionBD conexionBD;
    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void login(ActionEvent event) throws SQLException, IOException {
        String username = cjUsername.getText().trim();
        String password = cjPassword.getText().trim();

        conexionBD = new ConexionBD();
        conexionBD.conectar();
        usuarioDAO = new UsuarioDAO(conexionBD);
        Usuario usuario = usuarioDAO.getUsuario(username, password);

        if (usuario == null) {

            new animatefx.animation. Tada(cjUsername).play();
            new animatefx.animation. Tada(cjPassword).play();

            org.controlsfx.control.Notifications
                    .create()
                    .title("Mensaje de Inicio de Sesión")
                    .text(usuarioDAO.getMensajeLogin()) // Aquí se usa el mensaje que se generó
                    .position(Pos.CENTER)
                    .showError();
            cjPassword.clear();
            cjUsername.clear();

            return;

        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/appinventario/Dashboard.fxml"));
        BorderPane borderPane = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(borderPane));
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/logo.jpeg")));
        stage.setTitle("Sistema Inventario ECLIPSE " + Sesion.getSesion(null).getNombre());
        stage.setMaximized(true);
        com.inventory.appinventario.util.Metodos.closeEffect(root);
        stage.show();
    }
}
