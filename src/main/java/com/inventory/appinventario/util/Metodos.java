package com.inventory.appinventario.util;

import com.inventory.appinventario.model.Cliente;
import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.model.Usuario;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.HeadlessException;

public class Metodos {

    public static void closeEffect(Node node) {
        final Stage stage = (Stage) node.getScene().getWindow();
        ScaleTransition st = new ScaleTransition(Duration.millis(100), node);
        st.setToX(0);
        st.setToY(0);
        st.setToZ(0);
        st.play();
        st.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
    }

    public static void changeSizeOnColumn(TableColumn tc, TableView table, int row) {
        try {
            Text title = new Text(tc.getText());

            double ancho = title.getLayoutBounds().getWidth()+50;

            Object value = null;
            for (int i = ((row==-1)?0:row); i < ((row==-1)?table.getItems().size():(row+1) ); i++) {
                value = tc.getCellData(i);

                if(value instanceof Double){
                    title = new Text((value == null) ?"": NumberFormat.getCurrencyInstance().format(value));
                }else if(value instanceof String){
                    title = new Text((value == null) ?"":value.toString());
                }

                if (title.getLayoutBounds().getWidth() > ancho) {
                    ancho = title.getLayoutBounds().getWidth() + 50;
                }
            }
            tc.setPrefWidth(ancho);
        } catch (HeadlessException ex) {
            System.err.println(ex);
        }
    }

    public static byte[] ImageToByte(Image image) {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        if (image != null) {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            try {
                ImageIO.write(bImage, "png", s);
            } catch (IOException ex) {
                Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        byte[] res = s.toByteArray();
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
        }

        return res;
    }

    public static void rotarError(TableView<Producto> node) {
        RotateTransition rt = new RotateTransition(Duration.millis(100), node);
        rt.setCycleCount(7);
        rt.setAutoReverse(true);
        rt.setFromAngle(-5);
        rt.setToAngle(5);
        rt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                node.setRotate(0);
            }
        });
        rt.play();
    }

    public static void rotarErrorUsuario(TableView<Usuario> node) {
        RotateTransition rt = new RotateTransition(Duration.millis(100), node);
        rt.setCycleCount(7);
        rt.setAutoReverse(true);
        rt.setFromAngle(-5);
        rt.setToAngle(5);
        rt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                node.setRotate(0);
            }
        });
        rt.play();
    }

    public static void rotarErrorCliente(TableView<Cliente> node) {
        RotateTransition rt = new RotateTransition(Duration.millis(100), node);
        rt.setCycleCount(7);
        rt.setAutoReverse(true);
        rt.setFromAngle(-5);
        rt.setToAngle(5);
        rt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                node.setRotate(0);
            }
        });
        rt.play();
    }
}
