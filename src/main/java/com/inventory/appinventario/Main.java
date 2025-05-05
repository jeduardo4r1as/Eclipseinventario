package com.inventory.appinventario;

import com.inventory.appinventario.dao.ComercioDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
        HBox anchorPane = fxmlLoader.load(getClass().getResource("Login.fxml"));

        primaryStage.setScene(new Scene(anchorPane));

        primaryStage.show();
    }

}

