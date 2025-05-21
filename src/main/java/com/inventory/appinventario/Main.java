package com.inventory.appinventario;

import com.inventory.appinventario.dao.ComercioDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.poi.poifs.property.Parent;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {

        try{
            com.inventory.appinventario.model.Comercio.getInstance(new ComercioDAO().getComercio());
        }catch (SQLException e){
            e.printStackTrace();
        }

        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        AnchorPane anchorPane = fxmlLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/logo.jpeg")));
        primaryStage.setScene(new Scene(anchorPane));

        primaryStage.setTitle("Login - Sistema Inventario ECLIPSE");
        primaryStage.setResizable(false); // ← Esto impide redimensionar
        primaryStage.show();
    }

}

